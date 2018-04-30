/**
 * Created by chenqiang on 2017/9/19.
 */
public class LineClassification {

    public static final int COLLINEAR = 0;            // both lines are parallel and overlap each other
    public static final int LINES_INTERSECT = 1;    // lines intersect, but their segments do not
    public static final int SEGMENTS_INTERSECT = 2;    // both line segments bisect each other
    public static final int A_BISECTS_B = 3;        // line segment B is crossed by line A
    public static final int B_BISECTS_A = 4;        // line segment A is crossed by line B
    public static final int PARALELL = 5;            // the lines are paralell
}
