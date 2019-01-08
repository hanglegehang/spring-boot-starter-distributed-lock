package cn.hang.distributed.lock.execption;

/**
 * @author lihang15
 * @description
 * @create 2019-01-04 15:41
 **/
public class DLockException extends RuntimeException {

    private String msg;

    public DLockException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
