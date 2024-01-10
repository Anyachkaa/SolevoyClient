package ru.itskekoff.bots.macro;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Data
public class Macro implements Serializable {
    private String name;
    private List<MacroRecord> records;

}
