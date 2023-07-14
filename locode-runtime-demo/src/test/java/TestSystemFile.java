import com.centit.support.file.FileSystemOpt;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

public class TestSystemFile {
    public static void main(String[] args) {
        List<File> files = FileSystemOpt.findFiles(
            "/Users/codefan/projects/centit/centit-dev-platform/locode-runtime-demo/src/main/resources/config/dictionary", "*.json");
        for(  File f :files) {
            System.out.println(StringUtils.substringBefore(f.getName(), '.'));
        }
    }
}
