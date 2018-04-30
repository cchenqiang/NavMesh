import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by chenqiang on 2017/9/19.
 * 多边形
 */
public class Polygon {

    private int vertexNum; //顶点数
    private List<Vector2D> vertexV;//顶点列表

    private Rectangle2D.Double rect;


    public Polygon(int vertexNum, List<Vector2D> vertexV) {
        this.vertexNum = vertexNum;
        this.vertexV = vertexV;
    }

    public List<Vector2D> getVertexV() {
        return vertexV;
    }

    public int getVertexNum() {
        return vertexNum;
    }

    /**
     * 是否是简单多边形
     *
     * @return
     */
    public boolean isSimplicity() {
        //边数组
        List<Line2D> edges = new ArrayList<Line2D>();
        int len = vertexV.size() - 1;

        for (int i = 0; i < len; i++) {
            edges.add(new Line2D(vertexV.get(i), vertexV.get(i + 1)));
        }
        edges.add(new Line2D(vertexV.get(len), vertexV.get(0)));

        // 是否有内交点
        Vector2D itsPt = new Vector2D();    //返回的交点
        for (Line2D testLine : edges) {
            for (int i = 0; i < edges.size(); i++) {
                if (!testLine.equals(edges.get(i))) {
                    if (testLine.intersection(edges.get(i), itsPt) == LineClassification.SEGMENTS_INTERSECT) {
                        //交点是两个线段的端点
                        if (itsPt.equals(testLine.getPointA()) || itsPt.equals(testLine.getPointB())
                                || itsPt.equals(edges.get(i).getPointA()) || itsPt.equals(edges.get(i).getPointB())) {
                            ;
                        } else {
                            return false;
                        }
                    }
                }
            }
        }
        return true;


    }

    /**
     * 将多边形的顶点按顺时针排序
     */
    public void cw() throws Exception {
        if (!this.isCW()) {    //如果为逆时针顺序， 反转为顺时针
            Collections.reverse(this.vertexV);
        }
    }

    /**
     * clockwise
     *
     * @return true -- clockwise; false -- counter-clockwise
     */
    public boolean isCW() throws Exception {
        return NavMeshInitor.isCW(this.vertexV);
    }

    /**
     * r=multiply(sp,ep,op),得到(sp-op)*(ep-op)的叉积
     * r>0:ep在矢量opsp的逆时针方向；
     * r=0：opspep三点共线；
     * r<0:ep在矢量opsp的顺时针方向
     *
     * @param sp
     * @param ep
     * @param op
     * @return
     */
    private double multiply(Vector2D sp, Vector2D ep, Vector2D op) {
        return ((sp.x - op.x) * (ep.z - op.z) - (ep.x - op.x) * (sp.z - op.z));
    }


    public Rectangle2D.Double rectangle() {
        if (vertexV == null || vertexV.isEmpty()) {
            return null;
        }
        if (rect != null) {
            return rect;
        }
        double lx = vertexV.get(0).getX();
        double rx = vertexV.get(0).getX();
        double ty = vertexV.get(0).getZ();
        double by = vertexV.get(0).getZ();

        Vector2D v;
        for (int i = 1; i < vertexV.size(); i++) {
            v = vertexV.get(i);
            if (v.x < lx) {
                lx = v.x;
            }
            if (v.x > rx) {
                rx = v.x;
            }
            if (v.z < ty) {
                ty = v.z;
            }
            if (v.z > by) {
                by = v.z;
            }
        }

        rect = new Rectangle2D.Double(lx, rx - lx, ty, by - ty);
        return rect;


    }

    /**
     * 合并两个多边形(Weiler-Athenton算法)
     *
     * @param polygon
     * @return null--两个多边形不相交，合并前后两个多边形不变
     * Polygon--一个新的多边形
     */
    public List<Polygon> union(Polygon polygon) {
        //包围盒不相交
        if (!rectangle().intersects(polygon.rectangle())) {
            return null;
        }

        //所有顶点和交点
        List<Node> cv0 = new ArrayList<Node>();//主多边形
        List<Node> cv1 = new ArrayList<Node>();//合并多边形
        //初始化
        Node node;
        for (int i = 0;
             i < this.vertexV.size();
             i++) {
            node = new Node(this.vertexV.get(i), false, true);
            if (i > 0) {
                cv0.get(i - 1).next = node;
            }
            cv0.add(node);
        }
        for (int j = 0;
             j < polygon.vertexV.size();
             j++) {
            node = new Node(polygon.vertexV.get(j), false, false);
            if (j > 0) {
                cv1.get(j - 1).next = node;
            }
            cv1.add(node);
        }

        //插入交点
        int insCnt = this.intersectPoint(cv0, cv1);
//			trace("cv0:", cv0);
//			var ttp:Node = cv0[0];
//			trace(ttp);
//			while (ttp.next != null) {
//				trace(ttp.next);
//				ttp = ttp.next;
//			}
//			trace("cv1:", cv1);
//			var ttp:Node = cv1[0];
//			trace(ttp);
//			while (ttp.next != null) {
//				trace(ttp.next);
//				ttp = ttp.next;
//			}

        //生成多边形
//			var rc:sat.Vector.<Vector2D> = new sat.Vector.<Vector2D>();
        if (insCnt > 0) {
            //顺时针序
            return linkToPolygon(cv0, cv1);
        } else {
            return null;
        }
    }

    /**
     * 生成多边形，顺时针序； 生成的内部孔洞多边形为逆时针序
     *
     * @param cv0
     * @param cv1
     * @return 合并后的结果多边形数组(可能有多个多边形)
     */
    private List<Polygon> linkToPolygon(List<Node> cv0, List<Node> cv1) {
//保存合并后的多边形数组
        List<Polygon> rtV = new ArrayList<Polygon>();
        //1. 选取任一没有被跟踪过的交点为始点，将其输出到结果多边形顶点表中．
        for (Node testNode : cv0) {
            if (testNode.i && !testNode.p) {
                List<Vector2D> rcNodes = new ArrayList<Vector2D>();

                while (testNode != null) {
                    testNode.p = true;

                    // 如果是交点
                    if (testNode.i) {
                        testNode.other.p = true;
                        if (!testNode.o) {    //该交点为进点（跟踪裁剪多边形边界）
                            if (testNode.isMain) {    //当前点在主多边形中
                                testNode = testNode.other;//切换到裁剪多边形中
                            }
                        } else {    //该交点为出点（跟踪主多边形边界）
                            if (!testNode.isMain) {//当前点在裁剪多边形中
                                testNode = testNode.other;    //切换到主多边形中
                            }
                        }
                    }
                    rcNodes.add(testNode.v);////// 如果是多边形顶点，将其输出到结果多边形顶点表中


                    if (testNode.next == null) {//末尾点返回到开始点
                        if (testNode.isMain) {
                            testNode = cv0.get(0);
                        } else {
                            testNode = cv1.get(0);
                        }
                    } else {
                        testNode = testNode.next;
                    }

                    //与首点相同，生成一个多边形
                    if (testNode.v.equals(rcNodes.get(0))) {
                        break;
                    }
                }
                rtV.add(new Polygon(rcNodes.size(), rcNodes));
            }

        }

        return null;
    }

    /**
     * 生成交点，并按顺时针序插入到顶点表中
     *
     * @param cv0 （in/out）主多边形顶点表，并返回插入交点后的顶点表
     * @param cv1 （in/out）合并多边形顶点表，并返回插入交点后的顶点表
     * @return 交点数
     */
    private int intersectPoint(List<Node> cv0, List<Node> cv1) {
        int insCnt = 0; //交点数

        boolean findEnd = false;
        Node startNode0 = cv0.get(0);
        Node startNode1;
        Line2D line0;
        Line2D line1;
        Vector2D ins;
        boolean hasIns;
        int result;//进出点判断结果

        while (startNode0 != null) {//主多边形
            if (startNode0.next == null) {//最后一个点，跟首点相连
                line0 = new Line2D(startNode0.v, cv0.get(0).v);
            } else {
                line0 = new Line2D(startNode0.v, startNode0.next.v);
            }
            startNode1 = cv1.get(0);
            hasIns = false;
            while (startNode1 != null) {//合并多边形
                if (startNode1.next == null) {
                    line1 = new Line2D(startNode1.v, cv1.get(0).v);
                } else {
                    line1 = new Line2D(startNode1.v, startNode1.next.v);
                }
                ins = new Vector2D();//接受放回的交点
                //有交点
                if (line1.intersection(line1, ins) == LineClassification.SEGMENTS_INTERSECT) {
                    //忽略交点已在顶点列表中的
                    if (this.getNodeIndex(cv0, ins) == -1) {
                        insCnt++;
                        ///////// 插入交点
                        Node node0 = new Node(ins, true, true);
                        Node node1 = new Node(ins, true, false);
                        cv0.add(node0);
                        cv1.add(node1);
                        //双向引用
                        node0.other = node1;
                        node1.other = node1;
//                        插入
                        node0.next = startNode0.next;
                        startNode0.next = node0;
                        node1.next = startNode1.next;
                        startNode1.next = node1;
                        //出点
                        if (line0.classifyPoint(line1.getPointB()) == PointClassification.RIGHT_SIDE) {
                            node0.o = true;
                            node1.o = true;
                        }
                        // 线段重合
                        hasIns = true;//有交点
                        //有交点，返回重新处理

                        break;
                    }
                }
                startNode1 = startNode1.next;
            }
            //如果没有交点继续处理下一个边，否则重新处理该点与插入的交点所形成的线段
            if (!hasIns) {
                startNode0 = startNode0.next;
            }
        }
        return insCnt;
    }

    /**
     * 取得节点的索引(合并多边形用)
     *
     * @param cv0
     * @param ins
     * @return
     */
    private int getNodeIndex(List<Node> cv0, Vector2D ins) {
        for (int i = 0; i < cv0.size(); i++) {
            if (cv0.get(i).equals(ins)) {
                return i;
            }
        }

        return -1;
    }


    /**
     * 顶点(合并多边形用)
     *
     * @author blc
     */
    class Node {
//	/** 原数组中的索引 */
//	public var index:int;
        /**
         * 坐标点
         */
        public Vector2D v;        //点
        /**
         * 是否是交点
         */
        public boolean i;
        /**
         * 是否已处理过
         */
        public boolean p = false;
        /**
         * 进点--false； 出点--true
         */
        public boolean o = false;
        /**
         * 交点的双向引用
         */
        public Node other;
        /**
         * 点是否在主多边形中
         */
        public boolean isMain;

        /**
         * 多边形的下一个点
         */
        public Node next;

        public Node(Vector2D pt, boolean isInters, boolean main) {
            this.v = pt;
            this.i = isInters;
            this.isMain = main;
        }

        public String toString() {
            return v.toString() + "-->交点：" + i + "出点：" + o + "主：" + isMain + "处理：" + p;
        }

//	public function equals(node:Node):Boolean {
//		return v.equals(node.v);
//	}
    }
}
