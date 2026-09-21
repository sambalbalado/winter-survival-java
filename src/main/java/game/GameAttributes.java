package game;

/**
 * Enum representing various attributes that actors in the game can have.
 * <p>
 * Attributes like HYDRATION and WARMTH can be used to track the actor's status
 * and can be modified by actions or items (e.g., drinking from a bottle or sleeping).
 * </p>
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public enum GameAttributes {
    /** Represents the actor's hydration level */
    HYDRATION,

    /** Represents the actor's warmth level */
    WARMTH
}
