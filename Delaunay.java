import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by chenqiang on 2017/9/19.
 */
public class Delaunay {

    private List<Polygon> polygonV;//所有多边形，第0个元素为区域外边界 (输入数据)

    private List<Vector2D> vertexV;        //所有顶点列表, 前outEdgeVecNmu个为外边界顶点
    private List<Line2D> edgeV;            //所有约束边

    private int outEdgeVecNum;            //区域外边界顶点数

    private LinkedList<Line2D> lineV;    //线段堆栈

    private List<Triangle> triangleV;    //生成的Delaunay三角形


    public List<Triangle> createDelaunay(List<Polygon> polyV) {
        //Step1. 	建立单元大小为 E*E 的均匀网格，并将多边形的顶点和边放入其中.
        //			其中 E=sqrt(w*h/n)，w 和 h 分别为多边形域包围盒的宽度、高度，n 为多边形域的顶点数 .
        initData(polyV);
        //Step2.	取任意一条外边界边 p1p2 .
        Line2D initEdge = getInitOutEdge();
        lineV.addFirst(initEdge);
        Line2D edge;
        do {
            edge = lineV.removeFirst();
            Vector2D p3 = findDT(edge);
            if (p3 == null) {
                continue;
            }
            Line2D line13 = new Line2D(edge.getPointA(), p3);
            Line2D line32 = new Line2D(p3, edge.getPointB());
            //Delaunay三角形放入输出数组
            Triangle trg = new Triangle(edge.getPointA(), edge.getPointB(), p3);
            triangleV.add(trg);

            //Step4.	如果新生成的边 p1p3 不是约束边，若已经在堆栈中，
            //			则将其从中删除；否则，将其放入堆栈；类似地，可处理 p3p2 .
            int index;
            if (indexOfVector(line13, this.edgeV) < 0) {
                index = indexOfVector(line13, lineV);
                if (index > -1) {
                    lineV.remove(index);
                } else {
                    lineV.addFirst(line13);
                }
            }

            if (indexOfVector(line32, this.edgeV) < 0) {
                index = indexOfVector(line32, lineV);
                if (index < 0) {
                    lineV.addFirst(line32);
                } else {
                    lineV.remove(index);
                }
            }
            //Step5.	若堆栈不空，则从中取出一条边，转Step3；否则，算法停止 .
        } while (lineV.size() > 0);

        return triangleV;
    }

    /**
     * 计算 DT 点
     *
     * @param line
     * @return
     */
    private Vector2D findDT(Line2D line) {

        Vector2D p1 = line.getPointA();
        Vector2D p2 = line.getPointB();

        //搜索所有可见点 			可以 按y方向搜索距线段终点最近的点

        // line的所有可见点
        List<Vector2D> allVPoint = new ArrayList<Vector2D>();
        for (Vector2D vt : this.vertexV) {
            if (isVisiblePointOfLine(vt, line)) {
                allVPoint.add(vt);
            }
        }
        if (allVPoint.size() == 0) {
            return null;
        }
        Vector2D p3 = allVPoint.get(0);
        boolean loopSign = false;

        do {
            loopSign = false;
            //Step1. 构造 Δp1p2p3 的外接圆 C（p1，p2，p3）及其网格包围盒 B（C（p1，p2，p3））
            Circle circle = this.circumCircle(p1, p2, p3);
            Rectangle2D.Double boundsBox = this.circleBounds(circle);
            //Step2. 依次访问网格包围盒内的每个网格单元：
            //		 若某个网格单元中存在可见点 p, 并且 ∠p1pp2 > ∠p1p3p2，则令 p3=p，转Step1；否则，转Step3.

            double angle132 = Math.abs(lineAngle(p1, p3, p2));// ∠p1p3p2
            for (Vector2D vec : allVPoint) {
                if (vec.equals(p1) || vec.equals(p2) || vec.equals(p3)) {
                    continue;
                }
                //不在包围盒中
                if (!boundsBox.contains(vec.x, vec.z)) {
                    continue;
                }

                double a1 = Math.abs(lineAngle(p1, vec, p2));
                if (a1 > angle132) {
                    /////转Step1
                    p3 = vec;
                    loopSign = true;
                    break;
                }

            }
///////转Step3
        } while (loopSign);

//Step3. 若当前网格包围盒内所有网格单元都已被处理完，
        //		 也即C（p1，p2，p3）内无可见点，则 p3 为的 p1p2 的 DT 点
        return p3;
    }

    /**
     * 判断线段是否是约束边
     *
     * @param line
     * @return 线段的索引，如果没有找到，返回-1
     */
    private int indexOfVector(Line2D line, List<Line2D> edgeV) {
        for (int i = 0; i < edgeV.size(); i++) {
            if (edgeV.get(i).equals(line)) {
                return i;
            }
        }
        return -1;
    }

    private Line2D getInitOutEdge() {
        Line2D initEdge = edgeV.get(0);
        //检查是否有顶点p在该边上，如果有则换一个外边界
        boolean loopSign;
        int loopIdx = 0;
        do {
            loopSign = false;
            loopIdx++;
            for (Vector2D testV : vertexV) {
                if (testV.equals(initEdge.getPointA()) || testV.equals(initEdge.getPointB())) continue;
                if (initEdge.classifyPoint(testV) == PointClassification.ON_LINE) {
                    loopSign = true;
                    initEdge = edgeV.get(loopIdx);
                    break;
                }
            }
        } while (loopSign && loopIdx < outEdgeVecNum - 1);//只取外边界
        return initEdge;
    }

    /**
     * 初始化数据
     *
     * @param polyV
     */
    private void initData(List<Polygon> polyV) {
//填充顶点和线列表
        vertexV = new ArrayList<Vector2D>();
        edgeV = new ArrayList<Line2D>();
        Polygon poly;
        for (int i = 0; i < polyV.size(); i++) {
            poly = polyV.get(i);
            putVertex(vertexV, poly.getVertexV());
            putEdge(edgeV, poly.getVertexV());
        }
        outEdgeVecNum = polyV.get(0).getVertexNum();
        lineV = new LinkedList<Line2D>();
        triangleV = new ArrayList<Triangle>();


    }


    /**
     * 根据srcV中的点生成多边形线段，并放入dstV
     *
     * @param dstV
     * @param srcV
     */
    private void putEdge(List<Line2D> dstV, List<Vector2D> srcV) {
        if (srcV.size() < 3) {//不是一个多边形
            return;
        }

        Vector2D p1 = srcV.get(0);
        Vector2D p2;

        for (int i = 0; i < srcV.size(); i++) {
            p2 = srcV.get(i);
            dstV.add(new Line2D(p1, p2));
            p1 = p2;
        }
        p2 = srcV.get(0);
        dstV.add(new Line2D(p1, p2));

    }

    /**
     * 将srcV中的点放入dstV
     *
     * @param dest
     * @param src
     */
    private void putVertex(List<Vector2D> dest, List<Vector2D> src) {
        dest.addAll(src);
    }

    /**
     * 返回顶角在o点，起始边为os，终止边为oe的夹角, 即∠soe (单位：弧度)
     * 角度小于pi，返回正值;   角度大于pi，返回负值
     */
    private double lineAngle(Vector2D s, Vector2D o, Vector2D e)

    {
        double cosfi, fi, norm;
        double dsx = s.x - o.x;
        double dsy = s.z - o.z;
        double dex = e.x - o.x;
        double dey = e.z - o.z;

        cosfi = dsx * dex + dsy * dey;
        norm = (dsx * dsx + dsy * dsy) * (dex * dex + dey * dey);
        cosfi /= Math.sqrt(norm);

        if (cosfi >= 1.0) return 0;
        if (cosfi <= -1.0) return -Math.PI;

        fi = Math.acos(cosfi);
        if (dsx * dey - dsy * dex > 0) return fi;      // 说明矢量os 在矢量 oe的顺时针方向
        return -fi;
    }

    /**
     * 返回圆的包围盒
     *
     * @param c
     * @return
     */
    private Rectangle2D.Double circleBounds(Circle c) {
        return new Rectangle2D.Double(c.center.x - c.r, c.center.z - c.r, c.r * 2, c.r * 2);
    }

    /**
     * 返回三角形的外接圆
     *
     * @param p1
     * @param p2
     * @param p3
     * @return
     */
    private Circle circumCircle(Vector2D p1, Vector2D p2, Vector2D p3)

    {
//			trace("circumCircle");
        double m1, m2, mx1, mx2, mz1, mz2;
        double dx, dz, rsqr, drsqr;
        double xc, zc, r;

			/* Check for coincident points */

        if (Math.abs(p1.z - p2.z) < NavMeshPathFinder.EPSILON && Math.abs(p2.z - p3.z) < NavMeshPathFinder.EPSILON) {
            return null;
        }

        m1 = -(p2.x - p1.x) / (p2.z - p1.z);
        m2 = -(p3.x - p2.x) / (p3.z - p2.z);
        mx1 = (p1.x + p2.x) / 2.0;
        mx2 = (p2.x + p3.x) / 2.0;
        mz1 = (p1.z + p2.z) / 2.0;
        mz2 = (p2.z + p3.z) / 2.0;

        if (Math.abs(p2.z - p1.z) < NavMeshPathFinder.EPSILON) {
            xc = (p2.x + p1.x) / 2.0;
            zc = m2 * (xc - mx2) + mz2;
        } else if (Math.abs(p3.z - p2.z) < NavMeshPathFinder.EPSILON) {
            xc = (p3.x + p2.x) / 2.0;
            zc = m1 * (xc - mx1) + mz1;
        } else {
            xc = (m1 * mx1 - m2 * mx2 + mz2 - mz1) / (m1 - m2);
            zc = m1 * (xc - mx1) + mz1;
        }

        dx = p2.x - xc;
        dz = p2.z - zc;
        rsqr = dx * dx + dz * dz;
        r = Math.sqrt(rsqr);

        return new Circle(new Vector2D(xc, zc), r);
    }

    /**
     * 判断点vec是否为line的可见点
     *
     * @param vec
     * @param line
     * @return true:vec是line的可见点
     */
    private boolean isVisiblePointOfLine(Vector2D vec, Line2D line) {
//			trace("isVisiblePointOfLine");
        if (vec.equals(line.getPointA()) || vec.equals(line.getPointB())) {
            return false;
        }

        //（1） p3 在边 p1p2 的右侧 (多边形顶点顺序为顺时针)；
        if (line.classifyPoint(vec) != PointClassification.RIGHT_SIDE) {
            return false;
        }

        //（2） p3 与 p1 可见，即 p1p3 不与任何一个约束边相交；
        if (!isVisibleIn2Point(line.getPointA(), vec)) {
            return false;
        }

        //（3） p3 与 p2 可见
        if (!isVisibleIn2Point(line.getPointB(), vec)) {
            return false;
        }

        return true;
    }

    /**
     * 点pa和pb是否可见(pa和pb构成的线段不与任何约束边相交，不包括顶点)
     *
     * @param pa
     * @param pb
     * @return
     */
    private boolean isVisibleIn2Point(Vector2D pa, Vector2D pb)

    {
        Line2D linepapb = new Line2D(pa, pb);
        Vector2D interscetVector = new Vector2D();
        //线段交点
        for (Line2D lineTmp : this.edgeV) {
            //两线段相交
            if (linepapb.intersection(lineTmp, interscetVector) == LineClassification.SEGMENTS_INTERSECT) {
                //交点是不是端点
                if (!pa.equals(interscetVector) && !pb.equals(interscetVector)) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 圆
     *
     * @author blc
     */
    class Circle {
        public Vector2D center;        //圆心
        public double r;            //半径

        public Circle(Vector2D center, double r) {
            this.center = center;
            this.r = r;
        }
    }


/**
 * blc
 Step1. 	建立单元大小为 E*E 的均匀网格，并将多边形的顶点和边放入其中.
 其中 E=sqrt(w*h/n)，w 和 h 分别为多边形域包围盒的宽度、高度，n 为多边形域的顶点数 .
 Step2.	取任意一条外边界边 p1p2 .
 Step3. 	计算 DT 点 p3，构成约束 Delaunay 三角形 Δp1p2p3 .
 Step4.	如果新生成的边 p1p3 不是约束边，若已经在堆栈中，
 则将其从中删除；否则，将其放入堆栈；类似地，可处理 p3p2 .
 Step5.	若堆栈不空，则从中取出一条边，转Step3；否则，算法停止 .
 */
/**
 我们称 p3 为 p1p2 的可见点，其必须满足下面
 三个条件：
 （1） p3 在边 p1p2 的右侧 (多边形顶点顺序为顺时针)；
 （2） p3 与 p1 可见，即 p1p3 不与任何一个约束边相交；
 （3） p3 与 p2 可见
 */
/**
 确定 DT 点的过程如下：
 Step1. 	构造 Δp1p2p3 的外接圆 C（p1，p2，p3）及其网格包围盒 B（C（p1，p2，p3））（如图 虚线所示）
 Step2.	依次访问网格包围盒内的每个网格单元：
 对未作当前趟数标记的网格单元进行搜索，并将其标记为当前趟数
 若某个网格单元中存在可见点 p, 并且 ∠p1pp2 > ∠p1p3p2，则令 p3=p1，转Step1；
 否则，转Step3.
 Step3. 	若当前网格包围盒内所有网格单元都已被标记为当前趟数，
 也即C（p1，p2，p3）内无可见点，则 p3 为的 p1p2 的 DT 点
 */

}
