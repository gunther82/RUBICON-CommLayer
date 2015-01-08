package cnr.rubicon.cml;
/**
 * This class is automatically generated by mig. DO NOT EDIT THIS FILE.
 * This class implements a Java interface to the 'CompleteMsg'
 * message type.
 */

public class CompleteMsg extends net.tinyos.message.Message {

    /** The default size of this message type in bytes. */
    public static final int DEFAULT_MESSAGE_SIZE = 19;

    /** The Active Message type associated with this message. */
    public static final int AM_TYPE = 33;

    /** Create a new CompleteMsg of size 19. */
    public CompleteMsg() {
        super(DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /** Create a new CompleteMsg of the given data_length. */
    public CompleteMsg(int data_length) {
        super(data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new CompleteMsg with the given data_length
     * and base offset.
     */
    public CompleteMsg(int data_length, int base_offset) {
        super(data_length, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new CompleteMsg using the given byte array
     * as backing store.
     */
    public CompleteMsg(byte[] data) {
        super(data);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new CompleteMsg using the given byte array
     * as backing store, with the given base offset.
     */
    public CompleteMsg(byte[] data, int base_offset) {
        super(data, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new CompleteMsg using the given byte array
     * as backing store, with the given base offset and data length.
     */
    public CompleteMsg(byte[] data, int base_offset, int data_length) {
        super(data, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new CompleteMsg embedded in the given message
     * at the given base offset.
     */
    public CompleteMsg(net.tinyos.message.Message msg, int base_offset) {
        super(msg, base_offset, DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new CompleteMsg embedded in the given message
     * at the given base offset and length.
     */
    public CompleteMsg(net.tinyos.message.Message msg, int base_offset, int data_length) {
        super(msg, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
    /* Return a String representation of this message. Includes the
     * message type name and the non-indexed field values.
     */
    public String toString() {
      String s = "Message <CompleteMsg> \n";
      try {
        s += "  [dest.pid=0x"+Long.toHexString(get_dest_pid())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [dest.devid=0x"+Long.toHexString(get_dest_devid())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [src.pid=0x"+Long.toHexString(get_src_pid())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [src.devid=0x"+Long.toHexString(get_src_devid())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [reliable=0x"+Long.toHexString(get_reliable())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [seq_num=0x"+Long.toHexString(get_seq_num())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [nbytes=0x"+Long.toHexString(get_nbytes())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [trans_id=0x"+Long.toHexString(get_trans_id())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [app_id=0x"+Long.toHexString(get_app_id())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [payload.msg_type=0x"+Long.toHexString(get_payload_msg_type())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [payload.sensorsBitmask=0x"+Long.toHexString(get_payload_sensorsBitmask())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [payload.period=0x"+Long.toHexString(get_payload_period())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      return s;
    }

    // Message-type-specific access methods appear below.

    /////////////////////////////////////////////////////////
    // Accessor methods for field: dest.pid
    //   Field type: int
    //   Offset (bits): 0
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'dest.pid' is signed (false).
     */
    public static boolean isSigned_dest_pid() {
        return false;
    }

    /**
     * Return whether the field 'dest.pid' is an array (false).
     */
    public static boolean isArray_dest_pid() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'dest.pid'
     */
    public static int offset_dest_pid() {
        return (0 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'dest.pid'
     */
    public static int offsetBits_dest_pid() {
        return 0;
    }

    /**
     * Return the value (as a int) of the field 'dest.pid'
     */
    public int get_dest_pid() {
        return (int)getUIntBEElement(offsetBits_dest_pid(), 16);
    }

    /**
     * Set the value of the field 'dest.pid'
     */
    public void set_dest_pid(int value) {
        setUIntBEElement(offsetBits_dest_pid(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'dest.pid'
     */
    public static int size_dest_pid() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'dest.pid'
     */
    public static int sizeBits_dest_pid() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: dest.devid
    //   Field type: int
    //   Offset (bits): 16
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'dest.devid' is signed (false).
     */
    public static boolean isSigned_dest_devid() {
        return false;
    }

    /**
     * Return whether the field 'dest.devid' is an array (false).
     */
    public static boolean isArray_dest_devid() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'dest.devid'
     */
    public static int offset_dest_devid() {
        return (16 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'dest.devid'
     */
    public static int offsetBits_dest_devid() {
        return 16;
    }

    /**
     * Return the value (as a int) of the field 'dest.devid'
     */
    public int get_dest_devid() {
        return (int)getUIntBEElement(offsetBits_dest_devid(), 16);
    }

    /**
     * Set the value of the field 'dest.devid'
     */
    public void set_dest_devid(int value) {
        setUIntBEElement(offsetBits_dest_devid(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'dest.devid'
     */
    public static int size_dest_devid() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'dest.devid'
     */
    public static int sizeBits_dest_devid() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: src.pid
    //   Field type: int
    //   Offset (bits): 32
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'src.pid' is signed (false).
     */
    public static boolean isSigned_src_pid() {
        return false;
    }

    /**
     * Return whether the field 'src.pid' is an array (false).
     */
    public static boolean isArray_src_pid() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'src.pid'
     */
    public static int offset_src_pid() {
        return (32 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'src.pid'
     */
    public static int offsetBits_src_pid() {
        return 32;
    }

    /**
     * Return the value (as a int) of the field 'src.pid'
     */
    public int get_src_pid() {
        return (int)getUIntBEElement(offsetBits_src_pid(), 16);
    }

    /**
     * Set the value of the field 'src.pid'
     */
    public void set_src_pid(int value) {
        setUIntBEElement(offsetBits_src_pid(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'src.pid'
     */
    public static int size_src_pid() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'src.pid'
     */
    public static int sizeBits_src_pid() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: src.devid
    //   Field type: int
    //   Offset (bits): 48
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'src.devid' is signed (false).
     */
    public static boolean isSigned_src_devid() {
        return false;
    }

    /**
     * Return whether the field 'src.devid' is an array (false).
     */
    public static boolean isArray_src_devid() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'src.devid'
     */
    public static int offset_src_devid() {
        return (48 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'src.devid'
     */
    public static int offsetBits_src_devid() {
        return 48;
    }

    /**
     * Return the value (as a int) of the field 'src.devid'
     */
    public int get_src_devid() {
        return (int)getUIntBEElement(offsetBits_src_devid(), 16);
    }

    /**
     * Set the value of the field 'src.devid'
     */
    public void set_src_devid(int value) {
        setUIntBEElement(offsetBits_src_devid(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'src.devid'
     */
    public static int size_src_devid() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'src.devid'
     */
    public static int sizeBits_src_devid() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: reliable
    //   Field type: byte
    //   Offset (bits): 64
    //   Size (bits): 8
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'reliable' is signed (false).
     */
    public static boolean isSigned_reliable() {
        return false;
    }

    /**
     * Return whether the field 'reliable' is an array (false).
     */
    public static boolean isArray_reliable() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'reliable'
     */
    public static int offset_reliable() {
        return (64 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'reliable'
     */
    public static int offsetBits_reliable() {
        return 64;
    }

    /**
     * Return the value (as a byte) of the field 'reliable'
     */
    public byte get_reliable() {
        return (byte)getSIntBEElement(offsetBits_reliable(), 8);
    }

    /**
     * Set the value of the field 'reliable'
     */
    public void set_reliable(byte value) {
        setSIntBEElement(offsetBits_reliable(), 8, value);
    }

    /**
     * Return the size, in bytes, of the field 'reliable'
     */
    public static int size_reliable() {
        return (8 / 8);
    }

    /**
     * Return the size, in bits, of the field 'reliable'
     */
    public static int sizeBits_reliable() {
        return 8;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: seq_num
    //   Field type: int
    //   Offset (bits): 72
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'seq_num' is signed (false).
     */
    public static boolean isSigned_seq_num() {
        return false;
    }

    /**
     * Return whether the field 'seq_num' is an array (false).
     */
    public static boolean isArray_seq_num() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'seq_num'
     */
    public static int offset_seq_num() {
        return (72 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'seq_num'
     */
    public static int offsetBits_seq_num() {
        return 72;
    }

    /**
     * Return the value (as a int) of the field 'seq_num'
     */
    public int get_seq_num() {
        return (int)getUIntBEElement(offsetBits_seq_num(), 16);
    }

    /**
     * Set the value of the field 'seq_num'
     */
    public void set_seq_num(int value) {
        setUIntBEElement(offsetBits_seq_num(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'seq_num'
     */
    public static int size_seq_num() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'seq_num'
     */
    public static int sizeBits_seq_num() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: nbytes
    //   Field type: short
    //   Offset (bits): 88
    //   Size (bits): 8
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'nbytes' is signed (false).
     */
    public static boolean isSigned_nbytes() {
        return false;
    }

    /**
     * Return whether the field 'nbytes' is an array (false).
     */
    public static boolean isArray_nbytes() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'nbytes'
     */
    public static int offset_nbytes() {
        return (88 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'nbytes'
     */
    public static int offsetBits_nbytes() {
        return 88;
    }

    /**
     * Return the value (as a short) of the field 'nbytes'
     */
    public short get_nbytes() {
        return (short)getUIntBEElement(offsetBits_nbytes(), 8);
    }

    /**
     * Set the value of the field 'nbytes'
     */
    public void set_nbytes(short value) {
        setUIntBEElement(offsetBits_nbytes(), 8, value);
    }

    /**
     * Return the size, in bytes, of the field 'nbytes'
     */
    public static int size_nbytes() {
        return (8 / 8);
    }

    /**
     * Return the size, in bits, of the field 'nbytes'
     */
    public static int sizeBits_nbytes() {
        return 8;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: trans_id
    //   Field type: short
    //   Offset (bits): 96
    //   Size (bits): 8
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'trans_id' is signed (false).
     */
    public static boolean isSigned_trans_id() {
        return false;
    }

    /**
     * Return whether the field 'trans_id' is an array (false).
     */
    public static boolean isArray_trans_id() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'trans_id'
     */
    public static int offset_trans_id() {
        return (96 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'trans_id'
     */
    public static int offsetBits_trans_id() {
        return 96;
    }

    /**
     * Return the value (as a short) of the field 'trans_id'
     */
    public short get_trans_id() {
        return (short)getUIntBEElement(offsetBits_trans_id(), 8);
    }

    /**
     * Set the value of the field 'trans_id'
     */
    public void set_trans_id(short value) {
        setUIntBEElement(offsetBits_trans_id(), 8, value);
    }

    /**
     * Return the size, in bytes, of the field 'trans_id'
     */
    public static int size_trans_id() {
        return (8 / 8);
    }

    /**
     * Return the size, in bits, of the field 'trans_id'
     */
    public static int sizeBits_trans_id() {
        return 8;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: app_id
    //   Field type: short
    //   Offset (bits): 104
    //   Size (bits): 8
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'app_id' is signed (false).
     */
    public static boolean isSigned_app_id() {
        return false;
    }

    /**
     * Return whether the field 'app_id' is an array (false).
     */
    public static boolean isArray_app_id() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'app_id'
     */
    public static int offset_app_id() {
        return (104 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'app_id'
     */
    public static int offsetBits_app_id() {
        return 104;
    }

    /**
     * Return the value (as a short) of the field 'app_id'
     */
    public short get_app_id() {
        return (short)getUIntBEElement(offsetBits_app_id(), 8);
    }

    /**
     * Set the value of the field 'app_id'
     */
    public void set_app_id(short value) {
        setUIntBEElement(offsetBits_app_id(), 8, value);
    }

    /**
     * Return the size, in bytes, of the field 'app_id'
     */
    public static int size_app_id() {
        return (8 / 8);
    }

    /**
     * Return the size, in bits, of the field 'app_id'
     */
    public static int sizeBits_app_id() {
        return 8;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: payload.msg_type
    //   Field type: short
    //   Offset (bits): 112
    //   Size (bits): 8
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'payload.msg_type' is signed (false).
     */
    public static boolean isSigned_payload_msg_type() {
        return false;
    }

    /**
     * Return whether the field 'payload.msg_type' is an array (false).
     */
    public static boolean isArray_payload_msg_type() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'payload.msg_type'
     */
    public static int offset_payload_msg_type() {
        return (112 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'payload.msg_type'
     */
    public static int offsetBits_payload_msg_type() {
        return 112;
    }

    /**
     * Return the value (as a short) of the field 'payload.msg_type'
     */
    public short get_payload_msg_type() {
        return (short)getUIntBEElement(offsetBits_payload_msg_type(), 8);
    }

    /**
     * Set the value of the field 'payload.msg_type'
     */
    public void set_payload_msg_type(short value) {
        setUIntBEElement(offsetBits_payload_msg_type(), 8, value);
    }

    /**
     * Return the size, in bytes, of the field 'payload.msg_type'
     */
    public static int size_payload_msg_type() {
        return (8 / 8);
    }

    /**
     * Return the size, in bits, of the field 'payload.msg_type'
     */
    public static int sizeBits_payload_msg_type() {
        return 8;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: payload.sensorsBitmask
    //   Field type: int
    //   Offset (bits): 120
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'payload.sensorsBitmask' is signed (false).
     */
    public static boolean isSigned_payload_sensorsBitmask() {
        return false;
    }

    /**
     * Return whether the field 'payload.sensorsBitmask' is an array (false).
     */
    public static boolean isArray_payload_sensorsBitmask() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'payload.sensorsBitmask'
     */
    public static int offset_payload_sensorsBitmask() {
        return (120 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'payload.sensorsBitmask'
     */
    public static int offsetBits_payload_sensorsBitmask() {
        return 120;
    }

    /**
     * Return the value (as a int) of the field 'payload.sensorsBitmask'
     */
    public int get_payload_sensorsBitmask() {
        return (int)getUIntBEElement(offsetBits_payload_sensorsBitmask(), 16);
    }

    /**
     * Set the value of the field 'payload.sensorsBitmask'
     */
    public void set_payload_sensorsBitmask(int value) {
        setUIntBEElement(offsetBits_payload_sensorsBitmask(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'payload.sensorsBitmask'
     */
    public static int size_payload_sensorsBitmask() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'payload.sensorsBitmask'
     */
    public static int sizeBits_payload_sensorsBitmask() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: payload.period
    //   Field type: int
    //   Offset (bits): 136
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'payload.period' is signed (false).
     */
    public static boolean isSigned_payload_period() {
        return false;
    }

    /**
     * Return whether the field 'payload.period' is an array (false).
     */
    public static boolean isArray_payload_period() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'payload.period'
     */
    public static int offset_payload_period() {
        return (136 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'payload.period'
     */
    public static int offsetBits_payload_period() {
        return 136;
    }

    /**
     * Return the value (as a int) of the field 'payload.period'
     */
    public int get_payload_period() {
        return (int)getUIntBEElement(offsetBits_payload_period(), 16);
    }

    /**
     * Set the value of the field 'payload.period'
     */
    public void set_payload_period(int value) {
        setUIntBEElement(offsetBits_payload_period(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'payload.period'
     */
    public static int size_payload_period() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'payload.period'
     */
    public static int sizeBits_payload_period() {
        return 16;
    }

}