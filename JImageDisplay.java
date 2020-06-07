package lab_5;

import javax.swing.JComponent;
import java.awt.*;
import java.awt.image.BufferedImage;

class JImageDisplay extends JComponent { // JImageDisplay расширяет возможности JComponent
    private BufferedImage buffer;  // Управляет изображением, содержимое которого можно записать
    public BufferedImage getImage() {
        return buffer;
    }
    public JImageDisplay(int width, int height) {
        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // Тип изображения TYPE_INT_RGB определяет, как цвета каждого пикселя будут представлены в изображении; значение TYPE_INT_RGB обозначает, что
        // красные, зеленые и синие компоненты имеют по 8 битов, представленные в формате int в указанном порядке
        Dimension imageDimension = new Dimension(width, height); //Отображение на экране изображения с заданными параметрами ширины и высоты
        super.setPreferredSize(imageDimension);
    }

    @Override //Переопределим метод
    public void paintComponent(Graphics g) { // Необходим для отрисовки компонентов
        super.paintComponent(g); // Необходим вызов метода суперкласса для того, чтобы объекты отображались правильно
        g.drawImage(buffer, 0, 0, buffer.getWidth(), buffer.getHeight(), null);
        ; // Передаем значение null для ImageObserver, поскольку данная функциональность не требуется
    }

    public void clearImage() { // Устанавливает все пиксели изображения в черный цвет (значение RGB 0)
        int[] array = new int[getWidth() * getHeight()];
        buffer.setRGB(0, 0, getWidth(), getHeight(), array, 0, 1);
    }

    public void drawPixel(int x, int y, int rgbColor) { // Устанавливает пиксель в определенный цвет
        buffer.setRGB(x, y, rgbColor);
    }
}