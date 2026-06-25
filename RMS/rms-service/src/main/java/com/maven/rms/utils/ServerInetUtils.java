package com.maven.rms.utils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ServerInetUtils {

	private static final String DEFAULT_IP = "127.0.0.1";

	/**
	 * Gets the machine's private IP address (like 192.168.x.x, 10.x.x.x, or
	 * 172.16-31.x.x)
	 * Falls back to localhost if no private IP is found
	 * 
	 * @return String representation of the IP address
	 */
	public String getServerIP() {
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();

				// Skip loopback and inactive interfaces
				if (networkInterface.isLoopback() || !networkInterface.isUp()) {
					continue;
				}

				Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();

					// Only consider IPv4 addresses
					if (addr instanceof Inet4Address) {
						String ip = addr.getHostAddress();

						// Check if it's a private IP address
						if (isPrivateIP(ip)) {
							log.debug("Found private IP: {} on interface: {}", ip, networkInterface.getName());
							return ip;
						}
					}
				}
			}

			log.warn("No private IP address found, returning default: {}", DEFAULT_IP);

		} catch (SocketException e) {
			log.error("Exception occurred while getting server IP", e);
		}

		return DEFAULT_IP;
	}

	/**
	 * Gets all IP addresses from all active network interfaces
	 * 
	 * @return List of all IP addresses (both IPv4 and IPv6)
	 */
	public List<String> getListOfServerIP() {
		List<String> listOfAddresses = new ArrayList<>();

		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();

				// Skip loopback and inactive interfaces
				if (iface.isLoopback() || !iface.isUp()) {
					continue;
				}

				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();
					String ip = addr.getHostAddress();
					listOfAddresses.add(ip);

					log.debug("Interface: {} ({}) - IP: {}",
							iface.getName(),
							iface.getDisplayName(),
							ip);
				}
			}

		} catch (SocketException e) {
			log.error("Exception occurred while getting list of server IPs", e);
		}

		return listOfAddresses;
	}

	/**
	 * Gets only private IPv4 addresses from all active network interfaces
	 * 
	 * @return List of private IPv4 addresses
	 */
	public List<String> getPrivateIPAddresses() {
		List<String> privateAddresses = new ArrayList<>();

		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();

				// Skip loopback and inactive interfaces
				if (iface.isLoopback() || !iface.isUp()) {
					continue;
				}

				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();

					// Only consider IPv4 addresses
					if (addr instanceof Inet4Address) {
						String ip = addr.getHostAddress();

						if (isPrivateIP(ip)) {
							privateAddresses.add(ip);
							log.debug("Found private IP: {} on interface: {} ({})",
									ip, iface.getName(), iface.getDisplayName());
						}
					}
				}
			}

		} catch (SocketException e) {
			log.error("Exception occurred while getting private IP addresses", e);
		}

		return privateAddresses;
	}

	/**
	 * Checks if an IP address is a private IP address
	 * Private IP ranges:
	 * - 10.0.0.0 to 10.255.255.255 (10.0.0.0/8)
	 * - 172.16.0.0 to 172.31.255.255 (172.16.0.0/12)
	 * - 192.168.0.0 to 192.168.255.255 (192.168.0.0/16)
	 * 
	 * @param ip the IP address to check
	 * @return true if the IP is private, false otherwise
	 */
	private boolean isPrivateIP(String ip) {
		if (ip == null || ip.isEmpty()) {
			return false;
		}

		try {
			String[] parts = ip.split("\\.");
			if (parts.length != 4) {
				return false;
			}

			int firstOctet = Integer.parseInt(parts[0]);
			int secondOctet = Integer.parseInt(parts[1]);

			// 10.0.0.0/8 - Class A private network
			if (firstOctet == 10) {
				return true;
			}

			// 172.16.0.0/12 - Class B private networks
			if (firstOctet == 172 && secondOctet >= 16 && secondOctet <= 31) {
				return true;
			}

			// 192.168.0.0/16 - Class C private networks
			if (firstOctet == 192 && secondOctet == 168) {
				return true;
			}

		} catch (NumberFormatException e) {
			log.debug("Invalid IP format: {}", ip);
			return false;
		}

		return false;
	}
}