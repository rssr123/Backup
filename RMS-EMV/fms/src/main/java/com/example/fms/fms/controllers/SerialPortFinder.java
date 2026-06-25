package com.example.fms.fms.controllers;

import com.fazecast.jSerialComm.SerialPort;

public class SerialPortFinder {
    public static void main(String[] args) {
        String portName = getCP210xPort();
        if (portName != null) {
            System.out.println("Silicon Labs CP210x USB to UART Bridge found on: " + portName);
        } else {
            System.out.println("No CP210x device found.");
        }
    }

    public static String getCP210xPort() {
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            if (port.getDescriptivePortName().toLowerCase().contains("silicon labs cp210x")) {
                return port.getSystemPortName(); // e.g., "COM3"
            }
        }
        return null; // No matching port found
    }
}

