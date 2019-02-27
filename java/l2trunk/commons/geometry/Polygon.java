package l2trunk.commons.geometry;

import java.util.ArrayList;
import java.util.List;

public class Polygon extends AbstractShape {
    private List<Point2D> points = new ArrayList<>();

    public Polygon add(int x, int y) {
        add(new Point2D(x, y));
        return this;
    }

    private Polygon add(Point2D p) {
        if (points.size() == 0) {
            min.y = p.y;
            min.x = p.x;
            max.x = p.x;
            max.y = p.y;
        } else {
            min.y = Math.min(min.y, p.y);
            min.x = Math.min(min.x, p.x);
            max.x = Math.max(max.x, p.x);
            max.y = Math.max(max.y, p.y);
        }
        points.add(p);
        return this;
    }

    @Override
    public Polygon setZmax(int z) {
        max.z = z;
        return this;
    }

    @Override
    public Polygon setZmin(int z) {
        min.z = z;
        return this;
    }

    @Override
    public boolean isInside(int x, int y) {
        if (x < min.x || x > max.x || y < min.y || y > max.y)
            return false;

        int hits = 0;
        int npoints = points.size();
        Point2D last = points.get(npoints - 1);

        Point2D cur;
        for (int i = 0; i < npoints; last = cur, i++) {
            cur = points.get(i);

            if (cur.y == last.y) {
                continue;
            }

            int leftx;
            if (cur.x < last.x) {
                if (x >= last.x) {
                    continue;
                }
                leftx = cur.x;
            } else {
                if (x >= cur.x) {
                    continue;
                }
                leftx = last.x;
            }

            double test1, test2;
            if (cur.y < last.y) {
                if (y < cur.y || y >= last.y) {
                    continue;
                }
                if (x < leftx) {
                    hits++;
                    continue;
                }
                test1 = x - cur.x;
                test2 = y - cur.y;
            } else {
                if (y < last.y || y >= cur.y) {
                    continue;
                }
                if (x < leftx) {
                    hits++;
                    continue;
                }
                test1 = x - last.x;
                test2 = y - last.y;
            }

            if (test1 < (test2 / (last.y - cur.y) * (last.x - cur.x))) {
                hits++;
            }
        }

        return ((hits & 1) != 0);
    }

    public boolean validate() {
        if (points.size() < 3)
            return false;

        // ??????????? ?? ????? ???? ??????????????????
        if (points.size() > 3)
            // ??????? ???? - ?????????? ??? ????? ??????????????
            for (int i = 1; i < points.size(); i++) {
                int ii = i + 1 < points.size() ? i + 1 : 0; // ?????? ????? ?????? ?????
                // ?????????? ???? - ?????????? ??? ????? ??????????????? ????? ???, ??? ?? ??????? ????? ? ????????
                for (int n = i; n < points.size(); n++)
                    if (Math.abs(n - i) > 1) {
                        int nn = n + 1 < points.size() ? n + 1 : 0; // ?????? ????? ?????? ?????
                        if (GeometryUtils.checkIfLineSegementsIntersects(points.get(i), points.get(ii), points.get(n), points.get(nn))) {
                            return false;
                        }
                    }
            }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < points.size(); i++) {
            sb.append(points.get(i));
            if (i < points.size() - 1)
                sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
