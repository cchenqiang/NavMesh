public class Cell extends Triangle {
    public Cell(Vector2D pointA, Vector2D pointB, Vector2D pointC) {
        super(pointA, pointB, pointC);
        init();
    }

    public int index;    //在数组中的索引值
    public int[] links;    // 与该三角型连接的三角型索引， -1表示改边没有连接

    public int sessionId;

    public double f;
    public double h;
    public boolean isOpen;
    public Cell parent;

    public int m_ArrivalWall;// the side we arrived through.
    public Vector2D[] m_WallMidpoint;//每个边的中点
    public double[] m_WallDistance;// the distances between each wall midpoint of sides (0-1, 1-2, 2-0)

    private void init() {
        links = new int[3];
        links[0] = (-1);
        links[1] = (-1);
        links[2] = (-1);


        calculateData();


        m_WallMidpoint = new Vector2D[3];
        m_WallDistance = new double[3];
        // compute the midpoint of each tempCellList wall
        m_WallMidpoint[0] = new Vector2D((pointA.x + pointB.x) / 2.0, (pointA.z + pointB.z) / 2.0);
        m_WallMidpoint[1] = new Vector2D((pointC.x + pointB.x) / 2.0, (pointC.z + pointB.z) / 2.0);
        m_WallMidpoint[2] = new Vector2D((pointC.x + pointA.x) / 2.0, (pointC.z + pointA.z) / 2.0);

        // compute the distances between the wall midpoints
        Vector2D wallVector;
        wallVector = m_WallMidpoint[0].subtract(m_WallMidpoint[1]);
        m_WallDistance[0] = wallVector.length();

        wallVector = m_WallMidpoint[1].subtract(m_WallMidpoint[2]);

        m_WallDistance[1] = wallVector.length();

        wallVector = m_WallMidpoint[2].subtract(m_WallMidpoint[0]);
        m_WallDistance[2] = wallVector.length();
    }

    /**
     * 获得两个点的相邻三角型
     *
     * @param pA
     * @param pB
     * @param caller
     * @return 如果提供的两个点是caller的一个边, 返回true
     */
    private boolean requestLink(Vector2D pA, Vector2D pB, Cell caller) {
        if (pointA.equals(pA)) {
            if (pointB.equals(pB)) {
                links[SIDE_AB] = caller.index;
                return (true);
            } else if (pointC.equals(pB)) {
                links[SIDE_CA] = caller.index;
                return (true);
            }
        } else if (pointB.equals(pA)) {
            if (pointA.equals(pB)) {
                links[SIDE_AB] = caller.index;
                return (true);
            } else if (pointC.equals(pB)) {
                links[SIDE_BC] = caller.index;
                return (true);
            }
        } else if (pointC.equals(pA)) {
            if (pointA.equals(pB)) {
                links[SIDE_CA] = caller.index;
                return (true);
            } else if (pointB.equals(pB)) {
                links[SIDE_BC] = caller.index;
                return (true);
            }
        }

        // we are not adjacent to the calling tempCellList
        //我们不与呼叫单元相邻
        return (false);
    }

    /**
     * 取得指定边的相邻三角型的索引
     *
     * @param side
     * @return
     */
    private int getLink(int side) {
        return links[side];
    }


    /**
     * 检查并设置当前三角型与cellB的连接关系（方法会同时设置cellB与该三角型的连接）
     *
     * @param cellB
     */
    public void checkAndLink(Cell cellB) {
        if (getLink(SIDE_AB) == -1 && cellB.requestLink(pointA, pointB, this)) {
            setLink(SIDE_AB, cellB);
        } else if (getLink(SIDE_BC) == -1 && cellB.requestLink(pointB, pointC, this)) {
            setLink(SIDE_BC, cellB);
        } else if (getLink(SIDE_CA) == -1 && cellB.requestLink(pointC, pointA, this)) {
            setLink(SIDE_CA, cellB);
        }
    }

    /**
     * 设置side指定的边的连接到caller的索引
     *
     * @param side
     * @param caller
     */
    private void setLink(int side, Cell caller) {
        links[side] = caller.index;
    }


    /**
     * 记录路径从上一个节点进入该节点的边（如果从终点开始寻路即为穿出边）
     *
     * @param index 路径上一个节点的索引
     */
    public int setAndGetArrivalWall(int index) {
        if (index == links[0]) {
            m_ArrivalWall = 0;
            return 0;
        } else if (index == links[1]) {
            m_ArrivalWall = 1;
            return 1;
        } else if (index == links[2]) {
            m_ArrivalWall = 2;
            return 2;
        }
        return -1;
    }


    /**
     * 计算估价（h）  Compute the A* Heuristic for this tempCellList given a Goal point
     *
     * @param goal
     */
    public void computeHeuristic(Vector2D goal) {
        // our heuristic is the estimated distance (using the longest axis delta)
        // between our tempCellList center and the goal location

        double XDelta = Math.abs(goal.x - center.x);
        double YDelta = Math.abs(goal.z - center.z);

//			h = Math.max(XDelta, YDelta);
        h = XDelta + YDelta;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

}
