package memory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;

public class Mmu {
    private ByteBuffer memory;

    public Mmu(int memoryByteSize){
        memory = ByteBuffer.allocate(memoryByteSize);
        memory.order(ByteOrder.LITTLE_ENDIAN);
    }

    //NOTE::Read 16 bits with memory formatted with little endianness
    public short readData(short address){
        return memory.getShort(address);
    }

    //NOTE::Set 16 bits to memory formatted with little endianness
    public void setData(short value, short address) {
        memory.putShort(address, value);
    }

    //NOTE::Reading opcode at current pc
    public byte readOpcode(short address){ return (byte)memory.getChar(address); }

    //NOTE::Reads the Space Invaders rom files
    //NOTE::Memory layout of the game
    /*
     * invaders.h 0x0000-0x07FF
     * invaders.g 0x0800-0x0FFF
     * invaders.f 0x1000-0x17FF
     * invaders.e 0x1800-0x1FFF
     */
    public void loadRom(){
        int startOfInvadersHRom = 0x0000;
        int endOfInvadersHRom = 0x07FF;

        int startOfInvadersGRom = 0x0800;
        int endOfInvadersGRom = 0x0FFF;

        int startOfInvadersFRom = 0x1000;
        int endOfInvadersFRom = 0x17FF;

        int startOfInvadersERom = 0x1800;
        int endOfInvadersERom = 0x1FFF;

        try{
            File fileH = new File("SpaceInvadersRom/Invaders.h");
            File fileG = new File("SpaceInvadersRom/Invaders.g");
            File fileF = new File("SpaceInvadersRom/Invaders.f");
            File fileE = new File("SpaceInvadersRom/Invaders.E");

            byte[] fileByteArrayH = Files.readAllBytes(fileH.toPath());
            byte[] fileByteArrayG = Files.readAllBytes(fileG.toPath());
            byte[] fileByteArrayF = Files.readAllBytes(fileF.toPath());
            byte[] fileByteArrayE = Files.readAllBytes(fileE.toPath());

            //Note::Reading Space Invaders H File
            for(int i = startOfInvadersHRom; i <= endOfInvadersHRom; i++)
                memory.put(i, fileByteArrayH[i - startOfInvadersHRom]);
            //Note::Reading Space Invaders G File
            for(int i = startOfInvadersGRom; i <= endOfInvadersGRom; i++)
                memory.put(i, fileByteArrayG[i- startOfInvadersGRom]);
            //NOTE::Reading Space Invaders F Rom
            for(int i = startOfInvadersFRom; i <= endOfInvadersFRom; i++)
                memory.put(i, fileByteArrayF[i - startOfInvadersFRom]);
            //NOTE::Reading Space Invaders E Rom
            for(int i = startOfInvadersERom; i <= endOfInvadersERom; i++)
                memory.put(i, fileByteArrayE[i - startOfInvadersERom]);
        }catch(NullPointerException | IOException e){
            System.out.println(e);
        }
    }

    @Override
    public String toString(){
        String string = "";

        for(int i = 0; i < memory.capacity(); i++) {
            string = string.concat(String.format("%-3x", memory.get(i)));
            if((i + 1) % 8 == 0)
                string = string.concat(" | ");
            if((i + 1) % 32 == 0)
                string = string.concat("\n");
        }

        return string;
    }
}
