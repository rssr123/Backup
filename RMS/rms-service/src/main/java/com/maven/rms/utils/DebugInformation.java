package com.maven.rms.utils;

public class DebugInformation extends Exception{
	
    public static boolean suppressStacktrace;
    
	public DebugInformation() {}
	public DebugInformation(String str) {
        super(str, null, suppressStacktrace, !suppressStacktrace);
	}
	
	@Override
    public String toString() {
        if (suppressStacktrace) {
            return getLocalizedMessage();
        } else {
            return super.toString();
        }
    }
}
