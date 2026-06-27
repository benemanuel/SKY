import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GenAssets {
    static final int CANVAS = 600;
    static final int CENTER = CANVAS / 2;
    static final int RADIUS = 260;
    static final int NUM_RADIUS = RADIUS - 48;        // numerals sit inside the minute track
    static final Color LIGHT_BG = new Color(0xF7F3EA);  // warm white dial, like the reference watch
    static final Color INK = new Color(0x14130F);       // near-black numerals/ticks for the light dial
    static final Color DARK_BG = new Color(0x0B1020);   // dark dial for the inverted (reversed) theme
    static final Color CREAM = new Color(0xF2E9D0);     // light numerals/ticks for the dark dial

    // Hebrew numerals ordered 12,11,10,...,1 so i=0 places 12 at angle 0 (top)
    static final String[] HEBREW = { "יב", "יא", "י", "ט", "ח", "ז", "ו", "ה", "ד", "ג", "ב", "א" };

    public static void main(String[] args) throws IOException {
        String outDir = args.length > 0 ? args[0] : ".";
        new File(outDir).mkdirs();

        // Light (default) theme: black numerals on a warm-white dial, matching the reference watch.
        BufferedImage dialLight = drawDial(LIGHT_BG, INK);
        ImageIO.write(dialLight, "PNG", new File(outDir, "dial_background.png"));
        System.out.println("Generated dial_background.png");
        ImageIO.write(drawCap(INK, LIGHT_BG), "PNG", new File(outDir, "center_cap.png"));
        System.out.println("Generated center_cap.png");

        // Dark (inverted) theme: light numerals on a dark dial, selectable via the color style option.
        BufferedImage dialDark = drawDial(DARK_BG, CREAM);
        ImageIO.write(dialDark, "PNG", new File(outDir, "dial_background_dark.png"));
        System.out.println("Generated dial_background_dark.png");
        ImageIO.write(drawCap(CREAM, DARK_BG), "PNG", new File(outDir, "center_cap_dark.png"));
        System.out.println("Generated center_cap_dark.png");

        // Hands (unused by the renderer, which draws them directly, but kept for reference)
        ImageIO.write(createHand(90, 12, INK), "PNG", new File(outDir, "hand_hour.png"));
        ImageIO.write(createHand(140, 10, INK), "PNG", new File(outDir, "hand_minute.png"));
        ImageIO.write(createHand(150, 6, INK), "PNG", new File(outDir, "hand_second.png"));
        System.out.println("Generated hand_*.png");

        // Preview matches the default theme (dark) shown in the watch face picker.
        ImageIO.write(dialDark, "PNG", new File(outDir, "preview.png"));
        System.out.println("Generated preview.png");
    }

    // Draw a dial with the given background and ink (outline/ticks/numerals) colors.
    static BufferedImage drawDial(Color bg, Color ink) {
        BufferedImage dial = new BufferedImage(CANVAS, CANVAS, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dial.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(bg);
        g.fillRect(0, 0, CANVAS, CANVAS);

        // Circle outline + minute tick marks (60 ticks, evenly spaced)
        g.setColor(ink);
        g.setStroke(new BasicStroke(2));
        g.drawOval(CENTER - RADIUS, CENTER - RADIUS, RADIUS * 2, RADIUS * 2);
        for (int i = 0; i < 60; i++) {
            double angle = Math.toRadians(i * 6.0 + 90.0);
            int outerX = (int) (CENTER + RADIUS * Math.cos(angle));
            int outerY = (int) (CENTER - RADIUS * Math.sin(angle));
            int innerX = (int) (CENTER + (RADIUS - 12) * Math.cos(angle));
            int innerY = (int) (CENTER - (RADIUS - 12) * Math.sin(angle));
            g.drawLine(innerX, innerY, outerX, outerY);
        }

        // Hebrew numerals, counterclockwise: HEBREW[i] is value (12,11,...,1); place value v at
        // math angle 90 - i*30 so high numerals (11,10,9) run down the right and low (1,2,3) down
        // the left, matching the genuine Altneu clock.
        g.setColor(ink);
        g.setFont(hebrewFont(56f));
        FontMetrics fm = g.getFontMetrics();
        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians(90.0 - i * 30.0);
            int x = (int) (CENTER + NUM_RADIUS * Math.cos(angle));
            int y = (int) (CENTER - NUM_RADIUS * Math.sin(angle));
            String text = HEBREW[i];
            g.drawString(text, x - fm.stringWidth(text) / 2, y + fm.getAscent() / 2);
        }
        g.dispose();
        return dial;
    }

    // Center cap: solid disc in `ring` with a small `hole`-colored pinhole at its center.
    static BufferedImage drawCap(Color ring, Color hole) {
        BufferedImage cap = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = cap.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(ring);
        g.fillOval(0, 0, 40, 40);
        g.setColor(hole);
        g.fillOval(14, 14, 12, 12);
        g.dispose();
        return cap;
    }

    // Frank Ruehl — the classic Hebrew serif used on the genuine Altneu clock. Load the TTF
    // directly so generation doesn't depend on the family being registered by name.
    static Font hebrewFont(float size) {
        // Note: Frankbd.ttf / FRANKB.TTF on Windows are Franklin Gothic (Latin, no Hebrew).
        // The genuine Frank Ruehl Hebrew faces are the Culmus (CLM) and Libre families.
        String[] candidates = {
            "C:/Windows/Fonts/FrankRuehlCLM-Bold.ttf",
            "C:/Windows/Fonts/FrankRuhlLibre-Bold.ttf",
            "C:/Windows/Fonts/frank.ttf",
        };
        for (String path : candidates) {
            File f = new File(path);
            if (!f.exists()) continue;
            try {
                return Font.createFont(Font.TRUETYPE_FONT, f).deriveFont(size);
            } catch (FontFormatException | IOException e) {
                System.err.println("Could not load " + path + ": " + e.getMessage());
            }
        }
        System.err.println("Frank Ruehl not found; falling back to Serif Bold");
        return new Font("Serif", Font.BOLD, (int) size);
    }

    static BufferedImage createHand(int length, int width, Color color) {
        BufferedImage img = new BufferedImage(width, length, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        // Center the hand horizontally on the image
        int startX = (width - Math.max(1, width / 2)) / 2;
        g.fillRect(startX, 0, Math.max(1, width / 2), length);
        g.dispose();
        return img;
    }
}
