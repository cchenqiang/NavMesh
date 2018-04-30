

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 初始化导航网格，
 * 现在需要提供多边形集合
 * 以后可能只需要提供点，自己组装多边形
 * <p>
 * 如果是给的多边形，由buildTriangle来生成地图
 * 如果给的是三角形，createCell来生成地图
 */
public class NavMeshInitor {

    private NavMeshInitor() {
    }

    /**
     * 构造三角形
     */
    /**
     * @param polygonList//所有多边形
     * @return
     */
    public static List<Triangle> buildTriangle(List<Polygon> polygonList) throws Exception {
//        polygonList 第一个元素是最外层的多边形
        unionAll(polygonList);
        Delaunay delaunay = new Delaunay();
        //生成的Delaunay三角形
        return delaunay.createDelaunay(polygonList);
    }

    public static List<Cell> createCell(List<Triangle> triangleList) {
        //用于寻路的数据
        List<Cell> cellList = new ArrayList<Cell>();
        int index = 0;
        for (Triangle triangle : triangleList) {
            Cell e = new Cell(triangle.getVertex(0), triangle.getVertex(1), triangle.getVertex(2));
            e.index = index;
            cellList.add(e);
            index++;
        }
        linkCells(cellList);
        return cellList;
    }


    /**
     * 给出的多边形可能会有重叠，
     * 先合并一把
     */
    public static void unionAll(List<Polygon> polygonList) throws Exception {
        for (int n = 1; n < polygonList.size(); n++) {
            Polygon p0 = polygonList.get(n);
            for (int m = 1; m < polygonList.size(); m++) {
                Polygon p1 = polygonList.get(m);
                if (p0 != p1 && p0.isCW() && p1.isCW()) {
                    List<Polygon> unionList = p0.union(p1);
                    if (unionList != null && unionList.size() > 0) {
                        polygonList.remove(p0);
                        polygonList.remove(p1);
                        polygonList.addAll(unionList);
                        n = 1;
                        break;
                    }

                }
            }
        }
    }

    private static void linkCells(List<Cell> cellList) {
        for (Cell cellA : cellList) {
            for (Cell cellB : cellList) {
                if (cellA != cellB) {
                    cellA.checkAndLink(cellB);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        List<Triangle> triangleList = getTriangles("C:\\Users\\Administrator.SKY-20170512NTD\\Desktop\\NavmeshData.txt");
        tempCellList = createCell(triangleList);
    }

    public static List<Polygon> getPolygons(String navMeshPath) throws IOException {
        File file = new File(navMeshPath);
        FileInputStream fileInputStream = new FileInputStream(file);
//        byte[] data = new byte[fileInputStream.available()];
//        fileInputStream.read(data);
//        JsonObject jsonObject = new JsonParser().parse(new String(data)).getAsJsonObject();
//        int idKey = 1;
//        JsonArray jsonArray;
//        JsonObject triangle;
        List<Polygon> polygonList = new ArrayList<Polygon>();
//        Vector2D vector1 = new Vector2D(-55, -51);
//        Vector2D vector2 = new Vector2D(-55, 47);
//        Vector2D vector3 = new Vector2D(45, 47);
//        Vector2D vector4 = new Vector2D(45, -51);
//        //增加首个
//        List<Vector2D> tempVectors = new ArrayList<>();
//        tempVectors.add(vector1);
//        tempVectors.add(vector2);
//        tempVectors.add(vector3);
//        tempVectors.add(vector4);
//        try {
//            cw(tempVectors);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        polygonList.add(new Polygon(4, tempVectors));
//
//        while (true) {
//            jsonArray = jsonObject.getAsJsonArray(idKey + "");
//            if (jsonArray == null) {
//                break;
//            }
//            tempVectors = new ArrayList<>();
//            triangle = jsonArray.get(0).getAsJsonObject();
//            tempVectors.add(createVector2D(triangle));
//            triangle = jsonArray.get(1).getAsJsonObject();
//            tempVectors.add(createVector2D(triangle));
//            triangle = jsonArray.get(2).getAsJsonObject();
//            tempVectors.add(createVector2D(triangle));
//            try {
//                cw(tempVectors);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            polygonList.add(new Polygon(3, tempVectors));
//            idKey++;
//        }
        return polygonList;
    }

    public static List<Triangle> getTriangles(String navMeshPath) throws IOException {
        File file = new File(navMeshPath);
        FileInputStream fileInputStream = new FileInputStream(file);
        return getTriangles(fileInputStream);
    }

    public static List<Triangle> getTriangles(InputStream navMeshPath) throws IOException {
        byte[] data = new byte[navMeshPath.available()];
        navMeshPath.read(data);
//        JsonObject jsonObject = new JsonParser().parse(new String(data)).getAsJsonObject();
////
////        NavMesh navMesh = new NavMesh();
////        navMesh.loadFromJson(parse);
//        int idKey = 1;
//        JsonArray jsonArray;
//        JsonObject triangle;
        List<Triangle> triangleList = new ArrayList<Triangle>();
//        List<Vector2D> tempVectors = new ArrayList<Vector2D>();
//        while (true) {
//            jsonArray = jsonObject.getAsJsonArray(idKey + "");
//            if (jsonArray == null) {
//                break;
//            }
//            tempVectors.clear();
//            triangle = jsonArray.get(0).getAsJsonObject();
//            tempVectors.add(createVector2D(triangle));
//            triangle = jsonArray.get(1).getAsJsonObject();
//            tempVectors.add(createVector2D(triangle));
//            triangle = jsonArray.get(2).getAsJsonObject();
//            tempVectors.add(createVector2D(triangle));
//            idKey++;
//            try {
//                cw(tempVectors);
//            } catch (Exception e) {
//                continue;
//            }
//            triangleList.add(new Triangle(tempVectors.get(0), tempVectors.get(1), tempVectors.get(2)));
//        }
        return triangleList;
    }

    public static List<Cell> tempCellList;

//    private static Vector2D createVector2D(JsonObject triangle) {
//        Vector2D a = new Vector2D();
//        a.x = triangle.get("getX").getAsDouble();
//        a.z = triangle.get("z").getAsDouble();
//        return a;
//    }

    /**
     * 将多边形的顶点按顺时针排序
     */
    public static void cw(List<Vector2D> vertexV) throws Exception {
        if (!isCW(vertexV)) {    //如果为逆时针顺序， 反转为顺时针
            Collections.reverse(vertexV);
        }
    }

    /**
     * clockwise
     *
     * @param vertexV
     * @return true -- clockwise; false -- counter-clockwise
     */
    public static boolean isCW(List<Vector2D> vertexV) throws Exception {
        if (vertexV == null || vertexV.isEmpty()) {
            return false;
        }

        //最上（y最小）最左（x最小）点， 肯定是一个凸点
        //寻找最上点
        Vector2D topPt = vertexV.get(0);
        int topPtId = 0;//点的索引

        for (int i = 1; i < vertexV.size(); i++) {
            if (topPt.z > vertexV.get(i).z) {
                topPt = vertexV.get(i);
                topPtId = i;

            } else if (topPt.z == vertexV.get(i).z) {//y相等时取x最小
                if (topPt.x > vertexV.get(i).x) {

                    topPt = vertexV.get(i);
                    topPtId = i;
                }
            }
        }
        //凸点的邻点
        int lastId = topPtId - 1 >= 0 ? topPtId - 1 : vertexV.size() - 1;
        int nextId = topPtId + 1 >= vertexV.size() ? 0 : topPtId + 1;
        Vector2D last = vertexV.get(lastId);
        Vector2D next = vertexV.get(nextId);

        //判断
        double r = multiply(last, next, topPt);
        if (r < 0) {
            return true;
        } else if (r == 0) {
            System.out.println("三点共线情况不存在，若三点共线则说明必有一点的y（斜线）或x（水平线）小于topPt");
            throw new Exception();
        }
        return false;
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
    private static double multiply(Vector2D sp, Vector2D ep, Vector2D op) {
        return ((sp.x - op.x) * (ep.z - op.z) - (ep.x - op.x) * (sp.z - op.z));
    }

}
