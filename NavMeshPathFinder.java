import java.util.*;

/**
 * 导航网格寻路
 */
public class NavMeshPathFinder {

    private int pathSessionId = 0;
    public static final double EPSILON = 0.000001;//精度
    private PriorityQueue<Cell> openList = new PriorityQueue<Cell>(10, new Comparator<Cell>() {
        @Override
        public int compare(final Cell a, final Cell b) {
            double v = a.f + a.h - b.f - b.h;
            if (v < 0)
                return -1;
            if (v > 0)
                return 1;
            return 0;
        }
    });
    private List<Cell> closeList = new ArrayList<Cell>();


    /**
     * @param startPoint          开始点
     * @param endPoint            结束点
     * @param map                 地图
     * @param movingListByNavMesh
     * @return
     */
    public boolean find(Vector2D startPoint, Vector2D endPoint, List<Cell> map, List<Vector2D> movingListByNavMesh) {


        movingListByNavMesh.clear();
        if (startPoint == endPoint) {
            return false;
        }
        double entityRadius = 1;

        pathSessionId++;
        Vector2D startPos = new Vector2D(startPoint.getX(), startPoint.getZ());
        Vector2D endPos = new Vector2D(endPoint.getX(), endPoint.getZ());
        Cell startCell = findClosetCell(map, startPos);
        Cell endCell = findClosetCell(map, endPos);

        if (startCell == null || endCell == null) {
            System.err.println("没有路径");
            return false;
        }


        if (startCell == endCell) {
            movingListByNavMesh.add(startPos);
            movingListByNavMesh.add(endPos);
        } else {
            return buildPath(startCell, startPos, endCell, endPos, map, entityRadius, movingListByNavMesh);
        }
        return true;
    }

    /**
     * Find the closest cell on the mesh to the given point
     */
    public Cell findClosetCell(List<Cell> cellList, Vector2D point) {
        Optional<Cell> first = cellList.parallelStream().filter(cell -> cell.isPointIn(point)).findFirst();
        return first.isPresent() ? first.get() : null;
    }

    private boolean buildPath(Cell startCell, Vector2D startPos,
                              Cell endCell, Vector2D endPos, List<Cell> m_CellVector,
                              double entityRadius, List<Vector2D> movingListByNavMesh) {
        openList.clear();
        closeList.clear();


        openList.offer(endCell);

        endCell.f = 0;
        endCell.h = 0;
        endCell.isOpen = false;
        endCell.parent = null;
        endCell.sessionId = pathSessionId;


        boolean foundPath = false;//是否找到路径
        Cell currNode;//当前节点
        Cell adjacentTmp;    //当前节点的邻接三角型


        while (openList.size() > 0) {
            // 1. 把当前节点从开放列表删除, 加入到封闭列表
            currNode = openList.poll();
            closeList.add(currNode);
            //路径是在同一个三角形内
            if (currNode == startCell) {
                foundPath = true;
                break;
            }


            // 2. 对当前节点相邻的每一个节点依次执行以下步骤:
            //所有邻接三角型
            int adjacentId;

            for (int i = 0; i < 3; i++) {
                adjacentId = currNode.links[i];
                // 3. 如果该相邻节点不可通行或者该相邻节点已经在封闭列表中,
                //    则什么操作也不执行,继续检验下一个节点;
                if (adjacentId < 0) {                        //不能通过
                    continue;
                } else {
                    adjacentTmp = m_CellVector.get(adjacentId);
                }
                if (adjacentTmp != null) {
                    if (adjacentTmp.sessionId != pathSessionId) {
                        // 4. 如果该相邻节点不在开放列表中,则将该节点添加到开放列表中,
                        //    并将该相邻节点的父节点设为当前节点,同时保存该相邻节点的G和F值;
                        adjacentTmp.sessionId = pathSessionId;
                        adjacentTmp.parent = currNode;
                        adjacentTmp.isOpen = true;

                        //H和F值
                        adjacentTmp.computeHeuristic(startPos);
                        adjacentTmp.f = currNode.f + adjacentTmp.m_WallDistance[Math.abs(i - currNode.m_ArrivalWall)];


                        //放入开放列表并排序
                        openList.offer(adjacentTmp);

                        // remember the side this caller is entering from
                        adjacentTmp.setAndGetArrivalWall(currNode.index);
                    } else {
                        // 5. 如果该相邻节点在开放列表中,
                        //    则判断若经由当前节点到达该相邻节点的G值是否小于原来保存的G值,
                        //    若小于,则将该相邻节点的父节点设为当前节点,并重新设置该相邻节点的G和F值
                        if (adjacentTmp.isOpen) {//已经在openList中
                            if (currNode.f + adjacentTmp.m_WallDistance[Math.abs(i - currNode.m_ArrivalWall)] < adjacentTmp.f) {
                                adjacentTmp.f = currNode.f;
                                adjacentTmp.parent = currNode;

                                // remember the side this caller is entering from
                                adjacentTmp.setAndGetArrivalWall(currNode.index);
                            }
                        } else {//已在closeList中
                            adjacentTmp = null;
                            continue;
                        }
                    }
                }
            }
        }
        //由网格路径生成Point数组路径
        if (foundPath) {
            getPath(startPos, endPos, endCell, entityRadius, movingListByNavMesh, m_CellVector);
            return true;
        }
        return false;
    }

    /**
     * 根据经过的三角形返回路径点(下一个拐角点法)
     *
     * @param start
     * @param end
     * @param endCell
     * @param entityRadius
     * @param m_CellVector
     * @return Point数组
     */
    private void getPath(Vector2D start, Vector2D end, Cell endCell, double entityRadius, List<Vector2D> pathArr, List<Cell> m_CellVector) {
//        logger.info("navmesh ------> closeSize {}", closeList.size());
        //经过的三角形
        List<Cell> cellPath = getCellPath();
        //没有路径
        if (cellPath == null || cellPath.size() == 0) {
            return;
        }

        //开始点
        pathArr.add(start);
        //起点与终点在同一三角形中
        if (cellPath.size() == 1) {
            pathArr.add(end);    //结束点
        } else
            method1(start, end, cellPath, pathArr, endCell, entityRadius);

    }

    private void method1(Vector2D start, Vector2D end, List<Cell> cellPath, List<Vector2D> pathArr, Cell endCell, double entityRadius) {
        //获取路点
        WayPoint wayPoint = new WayPoint(start, cellPath.get(0));
        int count = 0;
        while (!wayPoint.getPosition().equals(end)) {
            wayPoint = this.getFurthestWayPoint(wayPoint, cellPath, end);
            Vector2D clone = wayPoint.getPosition().clone();
            clone.setX(clone.getX());
            clone.setZ(clone.getZ());
            if (!clone.equals(pathArr.get(pathArr.size() - 1))) {
                pathArr.add(clone);
            }
            count++;
            if (count > 100) {
//                logger.info("navMesh error 招路点 循环过大");
                break;
            }
        }
//        logger.info("---> {}",pathArr);
    }

    /**
     * 下一个拐点
     *
     * @param wayPoint 当前所在路点
     * @param cellPath 网格路径
     * @param end      终点
     * @return
     */
    private WayPoint getFurthestWayPoint(WayPoint wayPoint, List<Cell> cellPath, Vector2D end) {
        Vector2D startPt = wayPoint.getPosition();    //当前所在路径点

        Cell cell = wayPoint.getCell();

        Cell lastCell = cell;

        int startIndex = cellPath.indexOf(cell);    //开始路点所在的网格索引

        Line2D outSide = cell.sides[cell.m_ArrivalWall];    //路径线在网格中的穿出边
        Vector2D lastPtA = outSide.getPointA();
        Vector2D lastPtB = outSide.getPointB();
        Line2D lastLineA = new Line2D(startPt, lastPtA);
        Line2D lastLineB = new Line2D(startPt, lastPtB);
        Vector2D testPtA, testPtB;        //要测试的点
        for (int i = startIndex + 1; i < cellPath.size(); i++) {
            cell = cellPath.get(i);
            outSide = cell.sides[cell.m_ArrivalWall];
            if (i == cellPath.size() - 1) {
                testPtA = end;
                testPtB = end;
            } else {
                testPtA = outSide.getPointA();
                testPtB = outSide.getPointB();
            }

            if (!lastPtA.equals(testPtA)) {
                if (lastLineB.classifyPoint(testPtA) == PointClassification.RIGHT_SIDE) {
                    //路点
                    return new WayPoint(lastPtB, lastCell);
                } else {
                    if (lastLineA.classifyPoint(testPtA) != PointClassification.LEFT_SIDE) {
                        lastPtA = testPtA;
                        lastCell = cell;
                        //重设直线
                        lastLineA.setPointB(lastPtA);
//							lastLineB.setPointB(lastPtB);
                    }
                }
            }

            if (!lastPtB.equals(testPtB)) {
                if (lastLineA.classifyPoint(testPtB) == PointClassification.LEFT_SIDE) {
                    //路径点
                    return new WayPoint(lastPtA, lastCell);
                } else {
                    if (lastLineB.classifyPoint(testPtB) != PointClassification.RIGHT_SIDE) {
                        lastPtB = testPtB;
                        lastCell = cell;
                        //重设直线
//							lastLineA.setPointB(lastPtA);
                        lastLineB.setPointB(lastPtB);
                    }
                }
            }
        }
        return new WayPoint(end, cellPath.get(cellPath.size() - 1));    //终点
    }

    private List<Cell> getCellPath() {
        List<Cell> pth = new ArrayList<Cell>();

        Cell st = closeList.get(closeList.size() - 1);

        pth.add(st);
        while (st.parent != null) {
            pth.add(st.parent);
            st = st.parent;
        }
        return pth;
    }

}
