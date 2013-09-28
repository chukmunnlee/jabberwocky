/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.jabberwocky.impl.core;

/**
 *
 * @author project
 */
public class Utility {
    
    public static boolean isNullOrEmpty(String s) {
        return ((null == s) || (s.trim().length() <= 0));
    }
    
    
}
