package popo.pdfparse.framework.helpers;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class DataUtils {

    public static List<String> getMatchesDataList(String regEx, String text) {
        List<String> data = new ArrayList<>();
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            data.add(matcher.group());
        }
        return data;
    }

    public static double parseDouble(String amount) {
        return Double.parseDouble(amount.replace(",", ".").trim());
    }

    public static List<byte[]> convertImagesToBytes(List<RenderedImage> pdfImages) {
        List<byte[]> actualImages = new ArrayList<>();
        pdfImages.forEach(image -> {
            BufferedImage bufferedImage = new BufferedImage(
                    image.getColorModel(), (WritableRaster) image.getData(), true, new Hashtable<>());
            actualImages.add(imageToByteArray(bufferedImage));
        });
        return actualImages;
    }

    public static List<byte[]> convertImagesToBytes(String... files) {
        List<byte[]> expectedImages = new ArrayList<>();
        Arrays.asList(files).forEach(file -> {
            BufferedImage image = null;
            try {
                image = ImageIO.read(new File(file));
            } catch (IOException e) {
                log.fatal(ExceptionUtils.getStackTrace(e));
                e.printStackTrace();
            }
            if (null == image) {
                throw new NullPointerException("Image not found: " + file);
            }
            expectedImages.add(imageToByteArray(image));
        });
        return expectedImages;
    }

    private static byte[] imageToByteArray(BufferedImage bufferedImage) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] imageByteArray = null;
        try {
            ImageIO.write(bufferedImage, "jpg", outputStream);
            outputStream.flush();
            imageByteArray = outputStream.toByteArray();
            outputStream.close();
        } catch (IOException e) {
            log.fatal(ExceptionUtils.getStackTrace(e));
            e.printStackTrace();
        }
        return imageByteArray;
    }
}
