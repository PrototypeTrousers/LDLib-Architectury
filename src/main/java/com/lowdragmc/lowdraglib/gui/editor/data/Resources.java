package com.lowdragmc.lowdraglib.gui.editor.data;

import com.lowdragmc.lowdraglib.gui.editor.data.resource.Resource;
import com.lowdragmc.lowdraglib.gui.editor.runtime.AnnotationDetector;
import net.minecraft.nbt.CompoundTag;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author KilaBash
 * @date 2022/12/2
 * @implNote Resource
 */
public class Resources {

    public final Map<String, Resource<?>> resources;

    protected Resources() {
        this.resources = new LinkedHashMap<>();
        for (var wrapper : AnnotationDetector.REGISTER_RESOURCES) {
            var resource =  wrapper.creator().get();
            resources.put(resource.name(), resource);
        }
    }

    public Resources(Map<String, Resource<?>> resources) {
        this.resources = resources;
    }

    public static Resources emptyResource() {
        return new Resources(new LinkedHashMap<>());
    }

    public static Resources fromNBT(CompoundTag tag) {
        var resource = new Resources();
        resource.deserializeNBT(tag);
        return resource;
    }

    public static Resources defaultResource() { // default
        Resources resources = new Resources();
        resources.resources.values().forEach(Resource::buildDefault);
        return resources;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void merge(Resources resources) {
        this.resources.forEach((k, v) -> {
            if (resources.resources.containsKey(k)) {
                Resource f = resources.resources.get(k);
                v.merge(f);
            }
        });
    }

    public void load() {
        resources.values().forEach(Resource::onLoad);
    }

    public void dispose() {
        resources.values().forEach(Resource::unLoad);
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        resources.forEach((key, resource) -> tag.put(key, resource.serializeNBT()));
        return tag;
    }

    public void deserializeNBT(CompoundTag nbt) {
        resources.forEach((k, v) -> v.deserializeNBT(nbt.getCompound(k)));
    }


}
