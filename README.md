# Diffie-Hellman Key Exchange Implementation & Security Analysis

A comprehensive Java implementation of the Diffie-Hellman key exchange algorithm with DES encryption, featuring security vulnerability analysis and attack simulations.

## Project Overview

This project implements the foundational Diffie-Hellman key exchange protocol for secure communication over insecure channels. The implementation includes client-server messaging applications with graphical user interfaces, DES encryption integration, and comprehensive security testing including simulated attacks.

## Features

### Core Implementation
- **Diffie-Hellman Key Exchange**: Complete implementation using modular exponentiation and discrete logarithm principles
- **DES Encryption**: Symmetric encryption using the Data Encryption Standard for message confidentiality
- **Client-Server Architecture**: GUI-based messaging applications for real-time communication
- **Secure Key Management**: Automatic key generation, exchange, and shared secret computation

### Security Analysis
- **Man-in-the-Middle Attack Simulation**: Oscar component that intercepts and manipulates key exchanges
- **Brute Force Attack**: Systematic key recovery attempts against DES encryption
- **Side Channel Attack**: Timing analysis-based key recovery simulation
- **Network Traffic Analysis**: Wireshark integration for packet inspection and security verification

## Technical Stack

- **Language**: Java
- **Cryptography**: Custom DES implementation, BigInteger for large number operations
- **GUI Framework**: Java Swing
- **Testing**: JUnit 5 (Jupiter), Mockito for unit testing
- **Network Analysis**: Wireshark for traffic inspection
- **Build Tools**: Maven/Gradle compatible structure

## Project Structure

```
src/
├── businesslayer/
│   ├── abstracts/          # Interface definitions
│   └── concrete/           # Core algorithm implementations
├── datalayer/
│   ├── abstracts/          # Data layer interfaces
│   └── concrete/           # Client/Server managers
└── presentation/           # GUI applications
    ├── ClientApp.java      # Client-side messaging interface
    └── ServerApp.java      # Server-side management interface

attacks/
├── BruteForce/            # Brute force attack implementation
├── SideChannel/           # Side channel attack simulation
└── MITM/                 # Man-in-the-middle attack component
```

## Key Components

### Core Classes
- **`DiffieHellmanKey.java`**: Core key exchange algorithm implementation
- **`DataEncryptionStandard.java`**: DES encryption/decryption functionality
- **`ServerManager.java`**: Server-side connection and message management
- **`ClientManager.java`**: Client-side communication handling


### Prerequisites
- Java 8 or higher
- Maven or Gradle (optional, for dependency management)
- Wireshark (optional, for network analysis)

### Configuration

The implementation uses the following default parameters:
- **Prime (P)**: Large prime number for modular arithmetic
- **Generator (G)**: Primitive root of the prime
- **Key Size**: 56-bit for DES compatibility
- **Server Port**: 8080

These can be modified in the respective configuration files or class constants.

## Security Considerations

### Known Vulnerabilities
- **Small Key Sizes**: Vulnerable to brute force with insufficient key lengths
- **No Authentication**: Susceptible to man-in-the-middle attacks without additional security measures
- **Quantum Threat**: Potentially vulnerable to quantum algorithms like Shor's algorithm

### Mitigation Strategies
- Use minimum 2048-bit keys for production
- Implement certificate-based authentication
- Consider quantum-resistant alternatives for long-term security

## Attack Simulations

### Man-in-the-Middle (Oscar)
The Oscar component demonstrates how an attacker can intercept key exchanges:
- Intercepts public key transmissions
- Substitutes own public keys
- Decrypts, manipulates, and re-encrypts messages

### Brute Force
Systematic key recovery attempts:
- Iterates through all possible 56-bit DES keys
- Tests decryption against known plaintext patterns
- Demonstrates computational complexity of key recovery

### Side Channel
Timing-based attack simulation:
- Analyzes decryption timing variations
- Attempts key recovery through statistical analysis
- Highlights implementation-dependent vulnerabilities


## Educational Value

This project serves as a comprehensive educational resource for:
- Understanding cryptographic protocols
- Learning secure communication principles
- Exploring attack methodologies and countermeasures
- Hands-on experience with Java cryptographic implementations


## Team Members

- Oğuzhan Öztürk
- Gizem Öztürk
- Ghazaleh Alizadehbirjandi
- Anil Duman
- Ali Özay
- Sena Karamanlı Aydın

