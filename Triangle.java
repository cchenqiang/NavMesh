public class Triangle implements Cloneable {

    public static final int SIDE_AB = 0;
    public static final int SIDE_BC = 1;
    public static final int SIDE_CA = 2;

    protected Vector2D pointA;
    protected Vector2D pointB;
    protected Vector2D pointC;

    protected Vector2D center;//中心点
    public Line2D[] sides;// 三角型的3个边


    protected boolean dataCalculated = false;//中心点是否已计算

    public Triangle(Vector2D pointA, Vector2D pointB, Vector2D pointC) {
        setPoints(pointA, pointB, pointC);
    }

    public void setPoints(Vector2D pointA, Vector2D pointB, Vector2D pointC) {
        this.pointA = pointA.clone();
        this.pointB = pointB.clone();
        this.pointC = pointC.clone();
        dataCalculated = false;
    }


    /**
     * 计算中心点（3个顶点的平均值）
     */
    protected void calculateData() {

        if (center == null) {
            center = pointA.clone();
        } else
            center.setVector2D(pointA);
        //将三个点的x,y加起来除以3
        center.addLocal(pointB).addLocal(pointC).multLocal(1.0 / 3.0);

        //边
        if (sides == null) {
            sides = new Line2D[3];
        }


        sides[SIDE_AB] = new Line2D(pointA, pointB);
        sides[SIDE_BC] = new Line2D(pointB, pointC);
        sides[SIDE_CA] = new Line2D(pointC, pointA);


    }

    /**
     * 根据i返回顶点
     *
     * @param i the index of the point.
     * @return the point.
     */
    public Vector2D getVertex(int i) {
        switch (i) {
            case 0:
                return pointA;
            case 1:
                return pointB;
            case 2:
                return pointC;
            default:
                return null;
        }

    }

    /**
     * 根据i指定的索引设置三角形的顶点
     *
     * @param i     the index to place the point.
     * @param point the point to set.
     */
    public void setVertex(int i, Vector2D point) {
        switch (i) {
            case 0:
                pointA = point.clone();
                break;
            case 1:
                pointB = point.clone();
                break;
            case 2:
                pointC = point.clone();
                break;
        }
        dataCalculated = false;
    }


    /**
     * 取得指定索引的边(从0开始，顺时针)
     *
     * @param sideIndex
     * @return
     */
    public Line2D getSide(int sideIndex) {
        if (!dataCalculated) {
            calculateData();
        }

        return sides[sideIndex];
    }


    public Line2D[] getSides() {
        return sides;
    }

    /**
     * 测试给定点是否在三角型中
     *
     * @param testPoint
     * @return
     */
    public boolean isPointIn(Vector2D testPoint) {
        if (!dataCalculated) {
            calculateData();
        }

        // 点在所有边的右面
        int interiorCount = 0;
        for (int i = 0; i < 3; i++) {
            if (sides[i].classifyPoint(testPoint) != PointClassification.LEFT_SIDE) {
                interiorCount++;
            }
        }
        return (interiorCount == 3);
    }

    @Override
    public Triangle clone() {
        try {
            Triangle triangle = (Triangle) super.clone();
            triangle.setPoints(pointA, pointB, pointC);
            return triangle;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toString() {
        return "Triangle:" + pointA + "->" + pointB + "->" + pointC;
    }


    public Vector2D getCenter() {
        if (!dataCalculated) {
            calculateData();
        }
        return center;
    }


}
