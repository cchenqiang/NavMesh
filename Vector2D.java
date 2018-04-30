import java.awt.geom.Point2D;


public class Vector2D implements Cloneable {

    public double x;
    public double z;

    public Vector2D() {

    }


    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }


    public void setVector2D(Vector2D Vector2D) {
        this.x = Vector2D.x;
        this.z = Vector2D.z;

    }

    /**
     * returns (in radians) the angle represented by
     * this Vector2D as expressed by a conversion from rectangular coordinates (<code>getX</code>,&nbsp;<code>z</code>)
     * to polar coordinates (r,&nbsp;<i>theta</i>).
     * 返回（以弧度表示）表示的角度
     * 这个Vector2D由直角坐标转换（<code> getX </ code>，＆nbsp; <code> z </ code>）表示
     * 到极坐标（r，＆lt;＆gt;＆lt; i>）。
     *
     * @return the angle in radians. [-pi, pi)
     */
    public double getAngle() {
        return -Math.atan2(z, x);
    }

    /**
     * <code>lengthSquared</code> calculates the squared value of the
     * magnitude of the vector.
     * 计算的平方值
     * 载体的大小。
     *
     * @return the magnitude squared of the vector.
     */
    public double lengthSqared() {
        return Math.pow(x, 2) + Math.pow(z, 2);
    }

    /**
     * <code>length</code> calculates the magnitude of this vector.
     *
     * @return the length or magnitude of the vector.
     */
    public double length() {
        return Math.sqrt(lengthSqared());
    }

    /**
     * <code>distanceSquared</code> calculates the distance squared between
     * this vector and vector v.
     *
     * @param v the second vector to determine the distance squared.
     * @return the distance squared between the two vectors.
     */
    public double distanceSquared(Vector2D v) {
        double dx = x - v.x;
        double dy = z - v.z;
        return Math.pow(dx, 2) + Math.pow(dy, 2);
    }


    public Vector2D(double x, double y) {
        this.x = x;
        this.z = y;
    }

    /**
     * <code>negate</code> returns the negative of this vector. All values are
     * negated and set to a new vector.
     *
     * @return the negated vector.
     */
    public Vector2D negate() {
        return new Vector2D(-x, -z);
    }

    /**
     * <code>negateLocal</code> negates the internal values of this vector.
     *
     * @return this.
     */
    public Vector2D negateLocal() {
        x = -x;
        z = -z;
        return this;
    }

    /**
     * <code>add</code> adds a provided vector to this vector creating a
     * resultant vector which is returned. If the provided vector is null, null
     * is returned.
     *
     * @param Vector2D the vector to add to this.
     * @return the resultant vector.
     */
    public Vector2D add(Vector2D Vector2D) {
        if (null == Vector2D) {
            return null;
        }

        return new Vector2D(x + Vector2D.x, z + Vector2D.z);
    }

    /**
     * <code>addLocal</code> adds a provided vector to this vector internally,
     * and returns a handle to this vector for easy chaining of calls. If the
     * provided vector is null, null is returned.
     *
     * @param Vector2D the vector to add to this vector.
     * @return this
     */
    public Vector2D addLocal(Vector2D Vector2D) {

        if (null == Vector2D) {
            return null;
        }

        x += Vector2D.x;
        z += Vector2D.z;
        return this;
    }

    /**
     * <code>subtract</code> subtracts the values of a given vector from those
     * of this vector creating a new vector object. If the provided vector is
     * null, an exception is thrown.
     *
     * @param Vector2D the vector to subtract from this vector.
     * @return the result vector.
     */
    public Vector2D subtract(Vector2D Vector2D) {

        return new Vector2D(x - Vector2D.x, z - Vector2D.z);
    }

    /**
     * <code>subtractLocal</code> subtracts a provided vector to this vector
     * internally, and returns a handle to this vector for easy chaining of
     * calls. If the provided vector is null, null is returned.
     *
     * @param Vector2D the vector to subtract
     * @return this
     */
    public Vector2D subtractLocal(Vector2D Vector2D) {
        if (Vector2D == null) {
            return null;
        }
        x -= Vector2D.x;
        z -= Vector2D.z;
        return this;
    }

    /**
     * <code>dotProduct</code> calculates the dotProduct product of this vector with a
     * provided vector. If the provided vector is null, 0 is returned.
     *
     * @param Vector2D the vector to dotProduct with this vector.
     * @return the resultant dotProduct product of this vector and a given vector.
     */
    public double dot(Vector2D Vector2D) {
        if (Vector2D == null) {
            return 0;
        }

        return x * Vector2D.x + z * Vector2D.z;
    }

    /**
     * <code>mult</code> multiplies this vector by a scalar. The resultant
     * vector is returned.
     *
     * @param scalar the value to multiply this vector by.
     * @return the new vector.
     */
    public Vector2D mult(double scalar) {
        return new Vector2D(x * scalar, z * scalar);

    }

    /**
     * <code>multLocal</code> multiplies this vector by a scalar internally,
     * and returns a handle to this vector for easy chaining of calls.
     *
     * @param scalar the value to multiply this vector by.
     * @return this
     */
    public Vector2D multLocal(double scalar) {

        x *= scalar;
        z *= scalar;
        return this;

    }

    /**
     * <code>multLocal</code> multiplies a provided vector to this vector
     * internally, and returns a handle to this vector for easy chaining of
     * calls. If the provided vector is null, null is returned.
     *
     * @param vec the vector to mult to this vector.
     * @return this
     */
    public Vector2D multLocalV(Vector2D vec) {

        if (vec == null) {
            return null;
        }
        x *= vec.x;
        z *= vec.z;
        return this;
    }

    /**
     * Multiplies this Vector2D's getX and z by the scalar and stores the result in
     * product. The result is returned for chaining. Similar to
     * product=this*scalar;
     *
     * @param scalar  The scalar to multiply by.
     * @param product The Vector2D to store the result in.
     * @return product, after multiplication.
     */
    public Vector2D multV(double scalar, Vector2D product) {
        if (product == null) {
            product = new Vector2D();
        }
        product.x = x * scalar;
        product.z = z * scalar;
        return product;
    }

    /**
     * <code>divide</code> divides the values of this vector by a scalar and
     * returns the result. The values of this vector remain untouched.
     *
     * @param scalar the value to divide this vectors attributes by.
     * @return the result <code>sat.Vector</code>.
     */
    public Vector2D divide(double scalar) {
        return new Vector2D(x / scalar, z / scalar);
    }

    /**
     * <code>divideLocal</code> divides this vector by a scalar internally,
     * and returns a handle to this vector for easy chaining of calls. Dividing
     * by zero will result in an exception.
     *
     * @param scalar the value to divides this vector by.
     * @return this
     */
    public Vector2D divideLocal(double scalar) {

        x /= scalar;
        z /= scalar;
        return this;
    }

    /**
     * <code>normalize</code> returns the unit vector of this vector.
     *
     * @return unit vector of this vector.
     */
    public Vector2D normalize() {
        double length = length();
        if (length != 0) {
            return divide(length);
        }
        return divide(1);
    }

    /**
     * <code>normalizeLocal</code> makes this vector into a unit vector of
     * itself.
     *
     * @return this.
     */
    public Vector2D normalizeLocal() {
        double length = length();
        if (length != 0) {
            return divideLocal(length);
        }
        return divideLocal(1);
    }

    /**
     * <code>smallestAngleBetween</code> returns (in radians) the minimum
     * angle between two vectors. It is assumed that both this vector and the
     * given vector are unit vectors (iow, normalized).
     *
     * @param otherVector a unit vector to find the angle against
     * @return the angle in radians.
     */
    public double smallestAngleBetween(Vector2D otherVector) {
        double dotProduct = dot(otherVector);
        double angle = Math.acos(dotProduct);
        return angle;
    }

    /**
     * <code>angleBetween</code> returns (in radians) the angle required to
     * rotate a ray represented by this vector to lie colinear to a ray
     * described by the given vector. It is assumed that both this vector and
     * the given vector are unit vectors (iow, normalized).
     *
     * @param otherVector the "destination" unit vector
     * @return the angle in radians.
     */
    public double angleBetween(Vector2D otherVector) {
        double angle = Math.atan2(otherVector.z, otherVector.x) - Math.atan2(z, x);
        return angle;
    }

    /**
     * Sets this vector to the interpolation by changeAmnt from this to the
     * finalVec this=(1-changeAmnt)*this + changeAmnt * finalVec
     * 确定两个指定点之间的点。 参数 changeAmnt 确定新的内插点相对于参数 pt1 和 pt2 指定的两个端点所处的位置。
     *
     * @param finalVec   The final vector to interpolate towards
     * @param changeAmnt An amount between 0.0 - 1.0 representing a percentage change
     *                   from this towards finalVec
     */

    public void interpolate(Vector2D finalVec, double changeAmnt) {
        this.x = (1 - changeAmnt) * this.x + changeAmnt * finalVec.x;
        this.z = (1 - changeAmnt) * this.z + changeAmnt * finalVec.z;
    }

    public Vector2D interpolateLocal(Vector2D beginVec, Vector2D finalVec, double changeAmnt) {
        this.x = (1.0F - changeAmnt) * beginVec.x + changeAmnt * finalVec.x;
        this.z = (1.0F - changeAmnt) * beginVec.z + changeAmnt * finalVec.z;
        return this;
    }


    /**
     * <code>zero</code> resets this vector's data to zero internally.
     */
    public void zero() {
        this.x = this.z = 0;
    }

    /**
     * Saves this Vector2D into the given double[] object.
     *
     * @param arr The double[] to take this Vector2D. If null, a new double[2] is created.
     * @return The array, with X, Y double values in that order
     */
    public double[] toArray(double[] arr) {
        if (arr == null) {
            arr = new double[2];
        }
        arr[0] = x;
        arr[1] = z;
        return arr;
    }

    public Point2D.Double toPoint() {
        return new Point2D.Double(x, z);
    }

    /**
     * 绕原点旋转
     *
     * @param angle
     * @param cw
     */
    public void rotateAroundOrigin(double angle, boolean cw) {
        if (cw) {
            angle = -angle;
        }
        double newX = Math.cos(angle) * x - Math.sin(angle) * z;
        double newY = Math.sin(angle) * x + Math.cos(angle) * z;
        this.x = newX;
        this.z = newY;
    }

    public boolean equals(Vector2D Vector2D) {
        return Math.abs(x - Vector2D.x) < NavMeshPathFinder.EPSILON && Math.abs(z - Vector2D.z) < NavMeshPathFinder.EPSILON;
    }

    public Vector2D clone() {
        try {
            return (Vector2D) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toString() {
        return "(" + x + "," + z + ")";
    }
}
