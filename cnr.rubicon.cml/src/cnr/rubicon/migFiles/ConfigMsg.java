package cnr.rubicon.migFiles;
/**
 * This class is automatically generated by mig. DO NOT EDIT THIS FILE.
 * This class implements a Java interface to the 'ConfigMsg'
 * message type.
 */

public class ConfigMsg extends net.tinyos.message.Message {

    /** The default size of this message type in bytes. */
    public static final int DEFAULT_MESSAGE_SIZE = 3;

    /** The Active Message type associated with this message. */
    public static final int AM_TYPE = 37;

    /** Create a new ConfigMsg of size 3. */
    public ConfigMsg() {
        super(DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /** Create a new ConfigMsg of the given data_length. */
    public ConfigMsg(int data_length) {
        super(data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new ConfigMsg with the given data_length
     * and base offset.
     */
    public ConfigMsg(int data_length, int base_offset) {
        super(data_length, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new ConfigMsg using the given byte array
     * as backing store.
     */
    public ConfigMsg(byte[] data) {
        super(data);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new ConfigMsg using the given byte array
     * as backing store, with the given base offset.
     */
    public ConfigMsg(byte[] data, int base_offset) {
        super(data, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new ConfigMsg using the given byte array
     * as backing store, with the given base offset and data length.
     */
    public ConfigMsg(byte[] data, int base_offset, int data_length) {
        super(data, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new ConfigMsg embedded in the given message
     * at the given base offset.
     */
    public ConfigMsg(net.tinyos.message.Message msg, int base_offset) {
        super(msg, base_offset, DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new ConfigMsg embedded in the given message
     * at the given base offset and length.
     */
    public ConfigMsg(net.tinyos.message.Message msg, int base_offset, int data_length) {
        super(msg, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
    /* Return a String representation of this message. Includes the
     * message type name and the non-indexed field values.
     */
    public String toString() {
      String s = "Message <ConfigMsg> \n";
      try {
        s += "  [type=0x"+Long.toHexString(get_type())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [value=0x"+Long.toHexString(get_value())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      return s;
    }

    // Message-type-specific access methods appear below.

    /////////////////////////////////////////////////////////
    // Accessor methods for field: type
    //   Field type: short, unsigned
    //   Offset (bits): 0
    //   Size (bits): 8
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'type' is signed (false).
     */
    public static boolean isSigned_type() {
        return false;
    }

    /**
     * Return whether the field 'type' is an array (false).
     */
    public static boolean isArray_type() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'type'
     */
    public static int offset_type() {
        return (0 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'type'
     */
    public static int offsetBits_type() {
        return 0;
    }

    /**
     * Return the value (as a short) of the field 'type'
     */
    public short get_type() {
        return (short)getUIntBEElement(offsetBits_type(), 8);
    }

    /**
     * Set the value of the field 'type'
     */
    public void set_type(short value) {
        setUIntBEElement(offsetBits_type(), 8, value);
    }

    /**
     * Return the size, in bytes, of the field 'type'
     */
    public static int size_type() {
        return (8 / 8);
    }

    /**
     * Return the size, in bits, of the field 'type'
     */
    public static int sizeBits_type() {
        return 8;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: value
    //   Field type: int, unsigned
    //   Offset (bits): 8
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'value' is signed (false).
     */
    public static boolean isSigned_value() {
        return false;
    }

    /**
     * Return whether the field 'value' is an array (false).
     */
    public static boolean isArray_value() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'value'
     */
    public static int offset_value() {
        return (8 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'value'
     */
    public static int offsetBits_value() {
        return 8;
    }

    /**
     * Return the value (as a int) of the field 'value'
     */
    public int get_value() {
        return (int)getUIntBEElement(offsetBits_value(), 16);
    }

    /**
     * Set the value of the field 'value'
     */
    public void set_value(int value) {
        setUIntBEElement(offsetBits_value(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'value'
     */
    public static int size_value() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'value'
     */
    public static int sizeBits_value() {
        return 16;
    }

}