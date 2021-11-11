package com.biketelemetry.data;

import android.content.Context;
import android.os.FileUtils;

import androidx.annotation.NonNull;

import com.biketelemetry.R;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LocalFileListLoader implements FileListLoader{

    public List<TelemetryFileListEntry> getFiles(Context context) {
        return getFileList(context).stream()
            .map(LocalFileListLoader::createFile)
            .collect(Collectors.toList());
    }

    private static TelemetryFileListEntry createFile(File file) {
        return new TelemetryFileListEntry(file.getName(), file.length());
    }

    @NonNull
    private List<File> getFileList(Context context) {
        return Arrays.stream(context.fileList())
            .map(File::new)
            .collect(Collectors.toList());
    }


}
