package com.autoplanter;

import org.bukkit.plugin.java.JavaPlugin;

public final class AutoPlanter extends JavaPlugin {
    @Override
    public void onEnable() {
        // Dependencies
        AutoPlanterItem autoPlanterItem = new AutoPlanterItem(this);
        AutoPlanterBlock autoPlanterBlock = new AutoPlanterBlock(this);

        // Dependents
        new AutoPlanterRecipe(this, autoPlanterItem).register();
        new AutoPlanterListener(this, autoPlanterItem, autoPlanterBlock).register();
        new PlantTask(this, autoPlanterBlock).begin();
    }
}
