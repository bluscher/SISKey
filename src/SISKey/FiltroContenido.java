/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SISKey;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author e10934a
 */
public class FiltroContenido  implements FilenameFilter{
    private String contenido;
    
    public FiltroContenido(String c) {
        this.contenido = c;        
    }

    @Override
    public boolean accept(File file, String string) {
        return string.contains(contenido);
    }
    
}
