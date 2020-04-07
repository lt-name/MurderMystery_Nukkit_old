package name.mysterymurder.utils;

import cn.nukkit.level.Level;
import name.mysterymurder.MysteryMurder;

import java.io.*;


public class LevelFileReset {

    public boolean resetLevel(Level level) {
        MysteryMurder.getInstance().getServer().unloadLevel(level);
        //MysteryMurder.getInstance().getDataFolder()

        return true;
    }

    public void copyDir(String from, String to) {
        File src = new File(from);
        File dest = new File(to);
        File [] fileArray = src.listFiles();
        //判读那目标文件夹是否存在
        if (!dest.exists()) {
            dest.mkdirs();
        }
        for (File file : fileArray) {
            //判断是文件夹还是文件
            if (file.isDirectory()) {
                String dirName = file.getName();
                File newDest = new File(dest, dirName);
                //递归，用来复制源文件的所有文件夹(不含文件)
                copyDir(file.getPath(), newDest.getPath());
            } else {
                String fileName = file.getName();
                File destFile = new File(dest, fileName);
                copy(file, destFile);
            }
        }
    }

    public void copy(File file, File destFile) {
        //选择流，分别为输入、输出流
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(file);
            os = new FileOutputStream(destFile);
            byte [] dirsDatas = new byte[1024*100];//缓冲容器
            int len = -1;
            while((len = is.read(dirsDatas)) != -1) {
                os.write(dirsDatas, 0, len);
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //释放资源 原则: 分别关闭，先打开的后关闭
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
