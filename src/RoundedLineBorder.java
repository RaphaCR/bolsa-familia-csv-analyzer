import javax.swing.border.AbstractBorder;
import java.awt.*;

final class RoundedLineBorder extends AbstractBorder {
    private final Color color;
    private final int thickness;
    private final int arcSize;

    RoundedLineBorder(Color color, int thickness, int arcSize) {
        this.color = color;
        this.thickness = thickness;
        this.arcSize = arcSize;
    }

    @Override
    public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(thickness));
        g2.drawRoundRect(x, y, width - 1, height - 1, arcSize, arcSize);
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component component) {
        return new Insets(thickness, thickness, thickness, thickness);
    }
}
