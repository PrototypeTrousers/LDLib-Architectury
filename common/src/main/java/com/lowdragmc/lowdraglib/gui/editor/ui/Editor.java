package com.lowdragmc.lowdraglib.gui.editor.ui;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.editor.ILDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.data.IProject;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.util.TreeBuilder;
import com.lowdragmc.lowdraglib.gui.util.TreeNode;
import com.lowdragmc.lowdraglib.gui.widget.DialogWidget;
import com.lowdragmc.lowdraglib.gui.widget.MenuWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.io.File;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author KilaBash
 * @date 2022/11/30
 * @implNote MainPage
 */
public abstract class Editor extends WidgetGroup implements ILDLRegister {
    @Environment(EnvType.CLIENT)
    public static Editor INSTANCE;
    @Getter
    protected final File workSpace;
    @Getter
    protected IProject currentProject;
    @Getter
    protected MenuPanel menuPanel;
    @Getter
    protected StringTabContainer tabPages;
    @Getter
    protected ConfigPanel configPanel;
    @Getter
    protected ResourcePanel resourcePanel;
    @Getter
    protected WidgetGroup floatView;
    @Getter
    protected ToolPanel toolPanel;
    @Getter
    protected String copyType;
    @Getter
    protected Object copied;

    public Editor(String modID) {
        this(new File(LDLib.getLDLibDir(), "assets/" + modID));
        if (LDLib.isClient()) {
            if (!this.workSpace.exists() && !this.workSpace.mkdirs()) {
                LDLib.LOGGER.error("Failed to create work space for mod: " + modID);
            }
        }
    }

    public Editor(File workSpace) {
        super(0, 0, 10, 10);
        setClientSideWidget();
        this.workSpace = workSpace;
    }

    @Override
    public void setGui(ModularUI gui) {
        super.setGui(gui);
        if (isRemote()) {
            if (gui == null) {
                INSTANCE = null;
            } else {
                INSTANCE = this;
                getGui().registerCloseListener(() -> INSTANCE = null);
            }
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onScreenSizeUpdate(int screenWidth, int screenHeight) {
        setSize(new Size(screenWidth, screenHeight));
        super.onScreenSizeUpdate(screenWidth, screenHeight);
        this.clearAllWidgets();
        initEditorViews();
        loadProject(currentProject);
    }

    public void initEditorViews() {
        this.toolPanel = new ToolPanel(this);
        this.configPanel = new ConfigPanel(this);
        this.tabPages = new StringTabContainer(this);
        this.resourcePanel = new ResourcePanel(this);
        this.menuPanel = new MenuPanel(this);
        this.floatView = new WidgetGroup(0, 0, this.getSize().width, this.getSize().height);

        this.addWidget(this.tabPages);
        this.addWidget(this.toolPanel);
        this.addWidget(this.configPanel);
        this.addWidget(this.resourcePanel);
        this.addWidget(this.menuPanel);
        this.addWidget(this.floatView);
    }

    public DialogWidget openDialog(DialogWidget dialog) {
        this.addWidget(dialog);
        Position pos = dialog.getPosition();
        Size size = dialog.getSize();
        if (pos.x + size.width > getGui().getScreenWidth()) {
            dialog.addSelfPosition(pos.x + size.width - getGui().getScreenWidth(), 0);
        } else if (pos.x < 0) {
            dialog.addSelfPosition(-pos.x, 0);
        }
        if (pos.y + size.height > getGui().getScreenHeight()) {
            dialog.addSelfPosition(0, pos.y + size.height - getGui().getScreenHeight());
        } else if (pos.y < 0) {
            dialog.addSelfPosition(0, -pos.y);
        }
        return dialog;
    }

    public <T, C> MenuWidget<T, C> openMenu(double posX, double posY, TreeNode<T, C> menuNode) {
        var menu = new MenuWidget<>((int) posX, (int) posY, 14, menuNode)
                .setNodeTexture(MenuWidget.NODE_TEXTURE)
                .setLeafTexture(MenuWidget.LEAF_TEXTURE)
                .setNodeHoverTexture(MenuWidget.NODE_HOVER_TEXTURE);
        waitToAdded(menu.setBackground(MenuWidget.BACKGROUND));

        return menu;
    }

    public void openMenu(double posX, double posY, TreeBuilder.Menu menuBuilder) {
        if (menuBuilder == null) return;
        openMenu(posX, posY, menuBuilder.build())
                .setCrossLinePredicate(TreeBuilder.Menu::isCrossLine)
                .setKeyIconSupplier(TreeBuilder.Menu::getIcon)
                .setKeyNameSupplier(TreeBuilder.Menu::getName)
                .setOnNodeClicked(TreeBuilder.Menu::handle);
    }

    public void loadProject(IProject project) {
        if (currentProject != null) {
            currentProject.onClosed(this);
        }

        currentProject = project;
        tabPages.clearAllWidgets();
        toolPanel.clearAllWidgets();

        if (currentProject != null) {
            currentProject.onLoad(this);
        }
    }

    public void setCopy(String copyType, Object copied) {
        this.copied = copied;
        this.copyType = copyType;
    }

    public void ifCopiedPresent(String copyType, Consumer<Object> consumer) {
        if (Objects.equals(copyType, this.copyType)) {
            consumer.accept(copied);
        }
    }

}
