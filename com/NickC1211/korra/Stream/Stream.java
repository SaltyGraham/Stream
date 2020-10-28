// 
// Decompiled by Procyon v0.5.36
// 

package com.NickC1211.korra.Stream;

import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import com.projectkorra.projectkorra.ProjectKorra;
import java.util.Iterator;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.util.DamageHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.Sound;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.waterbending.util.WaterReturn;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.block.Block;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.WaterAbility;

public class Stream extends WaterAbility implements AddonAbility
{
    private long cooldown;
    private long duration;
    private double damage;
    private double range;
    private double startHealth;
    private TempBlock tempBlock;
    private Block sourceBlock;
    private Location location;
    private Location sourceLoc;
    
    public Stream(final Player player) {
        super(player);
        if (!this.bPlayer.canBend((CoreAbility)this)) {
            return;
        }
        if (!player.isSneaking()) {
            return;
        }
        if (!WaterReturn.hasWaterBottle(player)) {
            this.sourceBlock = BlockSource.getWaterSourceBlock(player, 8.0, ClickType.SHIFT_DOWN, true, true, true, true, true);
        }
        else {
            this.sourceBlock = player.getEyeLocation().clone().getBlock();
        }
        if (!WaterReturn.hasWaterBottle(player) && this.sourceBlock != BlockSource.getWaterSourceBlock(player, 8.0, ClickType.SHIFT_DOWN, true, true, true, true, true)) {
            this.sourceBlock = null;
        }
        if (this.sourceBlock == null) {
            return;
        }
        this.setFields();
        this.start();
    }
    
    public void setFields() {
        this.cooldown = ConfigManager.getConfig().getLong("ExtraAbilities.NickC1211.Stream.Cooldown");
        this.duration = ConfigManager.getConfig().getLong("ExtraAbilities.NickC1211.Stream.Duration");
        this.range = ConfigManager.getConfig().getDouble("ExtraAbilities.NickC1211.Stream.Range");
        this.damage = ConfigManager.getConfig().getDouble("ExtraAbilities.NickC1211.Whip.Damage");
        this.startHealth = this.player.getHealth();
        this.sourceLoc = this.sourceBlock.getLocation();
        if (this.sourceBlock.getLocation().getY() >= this.player.getLocation().add(0.0, 1.0, 0.0).getY()) {
            this.location = this.sourceBlock.getLocation().clone();
        }
        else {
            this.location = this.sourceBlock.getLocation().add(0.0, 1.0, 0.0).clone();
        }
        if (this.player.getLocation().distance(this.location) > 3.0 && this.player.isSneaking()) {
            final Vector first = GeneralMethods.getDirection(this.sourceLoc, this.player.getLocation().add(0.0, 1.0, 0.0).add(this.player.getEyeLocation().getDirection().multiply(3)));
            first.normalize().multiply(1);
            this.location.add(first);
        }
        if (WaterReturn.hasWaterBottle(this.player)) {
            WaterReturn.emptyWaterBottle(this.player);
        }
    }
    
    public void progress() {
        if (!this.bPlayer.canBendIgnoreCooldowns((CoreAbility)this)) {
            this.remove();
            return;
        }
        if (System.currentTimeMillis() - this.getStartTime() > this.duration) {
            this.remove();
            return;
        }
        if (!WaterReturn.hasWaterBottle(this.player) && this.sourceBlock.getType() != Material.WATER) {
            this.sourceBlock.setType(Material.AIR);
        }
        if (this.player.getLocation().distance(this.location) > this.range && !this.player.isSneaking()) {
            this.remove();
            return;
        }
        if (isTransparent(this.player, this.location.getBlock()) || (this.location.getBlock().getType() == Material.CACTUS && !this.location.getBlock().isLiquid())) {
            GeneralMethods.breakBlock(this.location.getBlock());
        }
        else if (this.location.getBlock().getType() != Material.AIR && !isWater(this.location.getBlock())) {
            this.remove();
            return;
        }
        if (this.player.getHealth() < this.startHealth - 2.0) {
            this.remove();
            return;
        }
        this.tempBlock = new TempBlock(this.location.getBlock(), Material.WATER, GeneralMethods.getWaterData(8));
        this.location.getWorld().playSound(this.location, Sound.BLOCK_WATER_AMBIENT, 0.2f, 0.8f);
        this.tempBlock.setRevertTime(700L);
        if (!this.player.isSneaking()) {
            this.location.add(this.player.getEyeLocation().getDirection());
        }
        if (this.player.isSneaking()) {
            final Vector back = GeneralMethods.getDirection(this.location, this.player.getLocation().add(0.0, 1.0, 0.0).add(this.player.getLocation().getDirection().multiply(3)));
            back.normalize().multiply(1);
            this.location.add(back);
        }
        for (final Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 2.0)) {
            if (!this.player.isSneaking() && this.player.getLocation().distance(this.location) > 4.0 && entity instanceof LivingEntity && entity.getEntityId() != this.player.getEntityId()) {
                final Location location = this.player.getEyeLocation();
                final Vector vector = location.getDirection();
                entity.setVelocity(vector.normalize().multiply(1.2f));
                DamageHandler.damageEntity(entity, this.damage, (Ability)this);
            }
            if (this.player.isSneaking() && this.player.getLocation().distance(this.location) > 4.0 && entity instanceof LivingEntity && entity.getEntityId() != this.player.getEntityId()) {
                final Location location = this.player.getEyeLocation();
                final Vector vector = location.getDirection();
                entity.setVelocity(vector.normalize().multiply(-1.2f));
                DamageHandler.damageEntity(entity, this.damage, (Ability)this);
            }
            if (this.player.getLocation().distance(this.location) <= 4.0 && entity instanceof LivingEntity && entity.getEntityId() != this.player.getEntityId()) {
                final Location location = this.player.getEyeLocation();
                final Vector vector = location.getDirection();
                entity.setVelocity(vector.normalize().multiply(1.5f));
                DamageHandler.damageEntity(entity, this.damage, (Ability)this);
            }
        }
        for (final Entity e : GeneralMethods.getEntitiesAroundPoint(this.location, 1.0)) {
            if (!this.player.isSneaking() && this.player.getLocation().distance(this.location) > 4.0 && e instanceof Entity && e.getEntityId() != this.player.getEntityId()) {
                final Location location = this.player.getEyeLocation();
                final Vector vector = location.getDirection();
                e.setVelocity(vector.normalize().multiply(-1.2f));
            }
            if (this.player.isSneaking() && this.player.getLocation().distance(this.location) > 4.0 && e instanceof Entity && e.getEntityId() != this.player.getEntityId()) {
                final Location location = this.player.getEyeLocation();
                final Vector vector = location.getDirection();
                e.setVelocity(vector.normalize().multiply(-1.2f));
            }
            if (this.player.getLocation().distance(this.location) <= 4.0 && e instanceof Entity && e.getEntityId() != this.player.getEntityId()) {
                final Location location = this.player.getEyeLocation();
                final Vector vector = location.getDirection();
                e.setVelocity(vector.normalize().multiply(1.2f));
            }
        }
    }
    
    public long getCooldown() {
        return this.cooldown;
    }
    
    public Location getLocation() {
        return this.location;
    }
    
    public String getName() {
        return "Stream";
    }
    
    public String getDescription() {
        return "Stream is a versatile move for waterbenders. This technique can be used to deflect projectiles and swipe close range threats, and when fired knocks your opponent backwards. Additionally the whip can be used to pull entities towards you. If the player is damaged while controlling the stream it will dissapate. ";
    }
    
    public String getInstructions() {
        return "(Stream) Hold Shift to select a source, then click to raise a stream of water that can be directed. (Whip) Release shift to fire, and hold shift to bring it back. ";
    }
    
    public boolean isHarmlessAbility() {
        return false;
    }
    
    public boolean isSneakAbility() {
        return true;
    }
    
    public void remove() {
        this.bPlayer.addCooldown((Ability)this);
        new WaterReturn(this.player, this.location.add(0.0, 1.0, 0.0).getBlock());
        super.remove();
    }
    
    public String getAuthor() {
        return "NickC1211";
    }
    
    public String getVersion() {
        return "v1.5";
    }
    
    public void load() {
        ProjectKorra.plugin.getServer().getPluginManager().registerEvents((Listener)new StreamListener(), (Plugin)ProjectKorra.plugin);
        ProjectKorra.log.info(String.valueOf(this.getName()) + " " + this.getVersion() + " by " + this.getAuthor() + " loaded!");
        ConfigManager.getConfig().addDefault("ExtraAbilities.NickC1211.Stream.Cooldown", (Object)6000);
        ConfigManager.getConfig().addDefault("ExtraAbilities.NickC1211.Stream.Duration", (Object)15000);
        ConfigManager.getConfig().addDefault("ExtraAbilities.NickC1211.Stream.Range", (Object)20);
        ConfigManager.getConfig().addDefault("ExtraAbilities.NickC1211.Stream.Damage", (Object)1);
        ConfigManager.getConfig().addDefault("ExtraAbilities.NickC1211.Stream.Harmless", (Object)true);
        ConfigManager.defaultConfig.save();
    }
    
    public void stop() {
        ProjectKorra.log.info(String.valueOf(this.getName()) + " " + this.getVersion() + "by" + this.getAuthor() + "disabled!");
    }
}
