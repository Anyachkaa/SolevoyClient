package ru.itskekoff.client.image;

import lombok.Data;

import java.awt.*;
import java.io.File;

public @Data class ImageObject {
    private final File file;
    private final Image image;
}
