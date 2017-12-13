// Edited by ar288 and cg248

/**
 * A pair consisting of an object and an integer value.
 *
 * @param <T>
 *        The type of the object.
 */
public class ObjectIntPair<T> {
  /**
   * The object.
   */
  public T first;

  /**
   * The integer value.
   */
  public int second;

  /**
   * Creates a new pair that consists of an object and an integer value.
   * 
   * @param first
   *        The object.
   * @param second
   *        The integer value.
   */
  public ObjectIntPair(T first, int second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public String toString() {
    return String.format("(%s, %d)", this.first, this.second);
  }
}
