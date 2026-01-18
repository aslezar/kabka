package dev.kabka.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KabkaEngineTest {
    
    @Test
    void testEngineStartStop() {
        KabkaEngine engine = new KabkaEngine();
        
        assertFalse(engine.isRunning(), "Engine should not be running initially");
        
        engine.start();
        assertTrue(engine.isRunning(), "Engine should be running after start");
        
        engine.stop();
        assertFalse(engine.isRunning(), "Engine should not be running after stop");
    }
    
    @Test
    void testMultipleStarts() {
        KabkaEngine engine = new KabkaEngine();
        
        engine.start();
        engine.start(); // Should be idempotent
        
        assertTrue(engine.isRunning());
        engine.stop();
    }
}
