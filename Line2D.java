public class Line2D implements Cloneable {

    private Vector2D pointA;    // Endpoint A of our line segment
    private Vector2D pointB;// Endpoint B of our line segment
    private Vector2D m_Normal;// 'normal' of the ray.
    // a vector pointing to the right-hand side of the line when viewed from PointA towards PointB
    private boolean m_NormalCalculated = false;


    public Line2D(Vector2D pointA, Vector2D pointB) {
        this.pointA = pointA;
        this.pointB = pointB;
    }

    public void setPointA(Vector2D pointA) {
        this.pointA = pointA.clone();
        m_NormalCalculated = false;
    }

    public void setPointB(Vector2D pointB) {
        this.pointB = pointB.clone();
        m_NormalCalculated = false;
    }

    public void setPoints(Vector2D pointA, Vector2D pointB) {
        this.pointA = pointA.clone();
        this.pointB = pointB.clone();
        m_NormalCalculated = false;
    }


    public Vector2D getNormal() {
        if (!m_NormalCalculated) {
            computeNormal();
        }
        return m_Normal;
    }

    /**
     * 计算法线
     */
    private void computeNormal() {

        m_Normal = getDirection();
        double oldValue = m_Normal.z;
        m_Normal.z = m_Normal.x;
        m_Normal.x = -oldValue;
        m_NormalCalculated = true;


    }

    /**
     * 给定点到直线的带符号距离，从a点朝向b点，右向为正，左向为负
     */
    public double signeDistance(Vector2D point) {
        if (!m_NormalCalculated) {
            computeNormal();
        }
        Vector2D v2f = point.subtract(pointA);
        Vector2D testVector = new Vector2D(v2f.getX(), v2f.getZ());
        return testVector.dot(m_Normal);
    }

    /**
     * 判断点与直线的关系，假设你站在a点朝向b点，
     * 则输入点与直线的关系分为：Left, Right or Centered on the line
     *
     * @param point 点
     * @return
     */
    public int classifyPoint(Vector2D point) {
        int result = PointClassification.ON_LINE;
        double distance = signeDistance(point);
        if (distance > NavMeshPathFinder.EPSILON) {
            result = PointClassification.RIGHT_SIDE;
        } else if (distance < -NavMeshPathFinder.EPSILON) {
            result = PointClassification.LEFT_SIDE;
        }
        return result;
    }


    /**
     * 判断两个直线关系
     * this line A = x0, y0 and B = x1, y1
     * other is A = x2, y2 and B = x3, y3
     *
     * @param other           另一条直线
     * @param pIntersectPoint (out)返回两线段的交点
     * @return
     */
    public int intersection(Line2D other, Vector2D pIntersectPoint) {
        double denom = (other.pointB.z - other.pointA.z) * (this.pointB.x - this.pointA.x)
                -
                (other.pointB.x - other.pointA.x) * (this.pointB.z - this.pointA.z);

        double u0 = (other.pointB.x - other.pointA.x) * (this.pointA.z - other.pointA.z)
                -
                (other.pointB.z - other.pointA.z) * (this.pointA.x - other.pointA.x);

        double u1 = (other.pointA.x - this.pointA.x) * (this.pointB.z - this.pointA.z)
                -
                (other.pointA.z - this.pointA.z) * (this.pointB.x - this.pointA.x);

        //if parallel
        if (denom == 0.0) {
            //if collinear
            if (u0 == 0.0 && u1 == 0.0)
                return LineClassification.COLLINEAR;
            else
                return LineClassification.PARALELL;
        } else {
            //check if they intersect
            u0 = u0 / denom;
            u1 = u1 / denom;

            double x = this.pointA.x + u0 * (this.pointB.x - this.pointA.x);
            double y = this.pointA.z + u0 * (this.pointB.z - this.pointA.z);

            if (pIntersectPoint != null) {
                pIntersectPoint.x = x; //(m_PointA.getX + (FactorAB * Bx_minus_Ax));
                pIntersectPoint.z = y; //(m_PointA.z + (FactorAB * By_minus_Ay));
            }

            // now determine the type of intersection
            if ((u0 >= 0.0) && (u0 <= 1.0) && (u1 >= 0.0) && (u1 <= 1.0)) {
                return LineClassification.SEGMENTS_INTERSECT;
            } else if ((u1 >= 0.0) && (u1 <= 1.0)) {
                return (LineClassification.A_BISECTS_B);
            } else if ((u0 >= 0.0) && (u0 <= 1.0)) {
                return (LineClassification.B_BISECTS_A);
            }

            return LineClassification.LINES_INTERSECT;

        }
    }

    public Vector2D getPointA() {
        return pointA;
    }

    public Vector2D getPointB() {
        return pointB;
    }

    /**
     * 直线长度
     *
     * @return
     */
    public double length() {
        double xDist = pointB.x - pointA.x;
        double yDist = pointB.z - pointA.z;
        return Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
    }

    /**
     * 直线方向
     *
     * @return
     */
    public Vector2D getDirection() {
        Vector2D pt = pointB.subtract(pointA);

        Vector2D direction = new Vector2D(pt.x, pt.z);
        return direction.normalize();
    }

    /**
     * 线段是否相等 （忽略方向）
     *
     * @param line
     * @return
     */
    public boolean equals(Line2D line) {
        return (pointA.equals(line.getPointA()) && pointB.equals(line.getPointB())) ||
                (pointA.equals(line.getPointB()) && pointB.equals(line.getPointA()));
    }

    public Line2D clone() {
        try {
            Line2D line2D = (Line2D) super.clone();
            line2D.pointA = this.pointA.clone();
            line2D.pointB = this.pointB.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toString() {
        return "Line:" + pointA + " -> " + pointB;
    }

}
