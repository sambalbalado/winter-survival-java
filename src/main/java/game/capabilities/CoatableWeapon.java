package game.capabilities;

import game.items.Coatable;

/**
 * Interface representing a weapon that can be coated with items.
 * Combines weapon action capabilities with coating functionality.
 *
 * @author Taabish Farooq Bhat
 * @version 2.0
 */
public interface CoatableWeapon extends WeaponWithActions, Coatable {

     /**
      * Determines if this weapon can be coated.
      * Default implementation returns true for all coatable weapons.
      *
      * @return true if the weapon can be coated, false otherwise
      */
     default boolean canBeCoated() {
          return true;
     }
}
