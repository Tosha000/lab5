package lab_5;

import javax.swing.*;
import java.awt.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

public class FractalExplorer {
    private int size;
    private JImageDisplay display;
    private FractalGenerator gen;
    private Rectangle2D.Double d2;
    public FractalExplorer(int size){
        this.size=size;
        gen=new Burning_Ship();
        d2= new Rectangle2D.Double();
        gen.getInitialRange(d2);
        display=new JImageDisplay(size,size);

    }
    public  void createAndShowGUI (){

        display.setLayout(new BorderLayout());
        JFrame JimageDisplay = new JFrame("Fractal Explorer");
        JimageDisplay.add(display,BorderLayout.CENTER);
        JButton resetButton = new JButton("Reset");
        ButtonHandler handler = new FractalExplorer.ButtonHandler();
        resetButton.addActionListener(handler);
        JimageDisplay.add(resetButton, BorderLayout.SOUTH);

        MouseHandler click = new MouseHandler();
        display.addMouseListener(click);// Операция закрытия окна по умолчанию:
        JimageDisplay.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JComboBox ComboBox = new JComboBox();
        FractalGenerator mandelbrot = new Mandelbrot();
        ComboBox.addItem(mandelbrot);
        FractalGenerator tricorn = new Tricorn();
        ComboBox.addItem(tricorn);
        FractalGenerator burning_Ship = new Burning_Ship();
        ComboBox.addItem(burning_Ship);
        //Создаем кнопку для выбора фрактала из коллекции
        ButtonHandler fractalChooser = new ButtonHandler();
        ComboBox.addActionListener(fractalChooser);
        JPanel DisplayPanel = new JPanel();
        JLabel myLabel = new JLabel("Fractal:");
        DisplayPanel.add(myLabel);
        DisplayPanel.add(ComboBox);
        JimageDisplay.add(DisplayPanel, BorderLayout.NORTH);
        //Создаем кнопку для сохранения изображения фрактала
        JButton saveButton = new JButton("Save");
        JPanel myBottomPanel = new JPanel();
        myBottomPanel.add(saveButton);
        myBottomPanel.add(resetButton);
        JimageDisplay.add(myBottomPanel, BorderLayout.SOUTH);

        ButtonHandler saveHandler = new ButtonHandler();
        saveButton.addActionListener(saveHandler);


        JimageDisplay.pack();
        JimageDisplay.setVisible(true);
        JimageDisplay.setResizable(false);
    }

    private void drawFractal() { // Метод для вывода на экран фрактала, должен циклически проходить через каждый пиксель в отображении (т.е. значения x и y будут меняться от 0 до размера отображения)
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) { //x, y - пиксельная координата; xCoord, yCoord - координата в пространстве фрактала
                // Получим координаты x и у, соответствующих координатам пикселя X и У
                double xCoord = gen.getCoord(d2.x, d2.x + d2.width, size, x);
                double yCoord = gen.getCoord(d2.y, d2.y + d2.height, size, y);
                // Вычислим количество итераций для соответствующих координат в области отображения фрактала
                int iteration = gen.numIterations(xCoord, yCoord);

                if (iteration == -1) { // Если число итераций равно -1 (т.е. точка не выходит за границы),установим пиксель в черный цвет (для rgb значение 0).
                    display.drawPixel(x, y, 0);
                }
                else { // Иначе выберем значение цвета, основанное на количестве итераций
                    // Воспользуемся цветовым пространством HSV: поскольку значение цвета
                    // варьируется от 0 до 1, получается плавная последовательность цветов от
                    // красного к желтому, зеленому, синему, фиолетовому и затем обратно к красному
                    float hue = 0.7f + (float) iteration / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    // Обновим отображение в соответствии с цветом для каждого пикселя
                    display.drawPixel(x, y, rgbColor);
                }
            }

        }
        display.repaint(); // Обновим JimageDisplay в соответствии с текущим изображением
    }
    // Внутренний класс для обработки событий от кнопок
    private class ButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Получение команды
            String command = e.getActionCommand();
            // Если команда - получить выпадающий список, то список выпадает, пользователь выбирает фрактал и он перерисовывается
            if (e.getSource() instanceof JComboBox) {
                JComboBox mySource = (JComboBox) e.getSource();
                gen = (FractalGenerator) mySource.getSelectedItem();
                gen.getInitialRange(d2);
                drawFractal();
            }
            // Если команда сбросить фрактал - сбросить его и перерисовать
            else if (command.equals("Reset")) {  //обнуление и перерисовка фрактала
                gen.getInitialRange(d2);
                drawFractal();
            }
            // Если команда сохранить фрактал - сохранить его ф ормате PNG на диск
            else if (command.equals("Save")) {
                JFileChooser chooser = new JFileChooser();
                // Сохранять только в формате PNG
                FileFilter extensionFilter = new FileNameExtensionFilter("PNG Images", "png");
                chooser.setFileFilter(extensionFilter);
                // Если файл хотят сохранить в формате не png, вернуть false
                chooser.setAcceptAllFileFilterUsed(false);

                // Значение типа int, которое указывает результат операции выбора файла
                int userSelection = chooser.showSaveDialog(display);

                //Если метод возвращает значение JfileChooser.APPROVE_OPTION, тогда можно продолжить операцию
                // сохранения файлов, в противном случае, если пользователь отменил операцию,
                // закончить данную обработку события без сохранения
                if (userSelection == JFileChooser.APPROVE_OPTION) {

                    //Класс javax.imageio.ImageIO обеспечивает простые операции загрузки и сохранения изображения
                    java.io.File file = chooser.getSelectedFile();
                    String file_name = file.toString();
                    // Попытка сохранить фрактал на диск
                    try {
                        BufferedImage displayImage = display.getImage();
                        javax.imageio.ImageIO.write(displayImage, "png", file);
                    }
                    // Проинформировать пользователя об ошибке через диалоговое окно
                    catch (Exception exception) {
                        JOptionPane.showMessageDialog(display, exception.getMessage(), "Cannot Save Image", JOptionPane.ERROR_MESSAGE);
                    }
                }
                // Если операция сохранения файла не APPROVE_OPTION
                else return;
            }
        }
    }
    // Внутренний класс для обработки событий от кнопки сброса. Обработчик сбрасывает
    // диапазон к начальному, определенному генератором, а затем перерисовает фрактал
    private class ResetHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            gen.getInitialRange(d2);
            drawFractal();
        }
    }

    // Внутренний класс для обработки событий с дисплея от мыши
    private class MouseHandler extends MouseAdapter implements MouseListener {

        @Override // Переопределим метод
        // При получении события о щелчке мышью, класс должен
        // отобразить пиксельные кооринаты щелчка в область фрактала, а затем вызвать
        // метод генератора recenterAndZoomRange с координатами, по которым щелкнули, и масштабом 0.5, что приведёт к увеличению фрактала
        public void mouseClicked(MouseEvent e) {
            // Получение координаты х области щелчка мыши
            int x = e.getX();
            double xCoord = gen.getCoord(d2.x, d2.x + d2.width, size, x);
            // Получение координаты у области щелчка мыши
            int y = e.getY();
            double yCoord = gen.getCoord(d2.y, d2.y + d2.height, size, y);
            // Увеличение фрактала
            gen.recenterAndZoomRange(d2, xCoord, yCoord, 0.5);
            // Перерисуем фрактал
            drawFractal();
        }
    }
    public static void main(String[] args)
    {
        FractalExplorer explorer = new FractalExplorer(500);
        explorer.createAndShowGUI();
        explorer.drawFractal();
    }
}
