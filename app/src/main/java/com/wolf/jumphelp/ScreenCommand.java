package com.wolf.jumphelp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Runtime.getRuntime;

/**
 * Created by Roye on 2018/1/9.
 */

public class ScreenCommand {
    //shell进程
    private Process process;
    //对应进程的3个流
    private InputStream successResult;
    private BufferedReader errorResult;
    private DataOutputStream os;

    Bitmap bitmap;
    //是否同步，true：run会一直阻塞至完成或超时。false：run会立刻返回
    private boolean bSynchronous;
    //表示shell进程是否还在运行
    private boolean bRunning = false;
    //同步锁
    ReadWriteLock lock = new ReentrantReadWriteLock();

    //保存执行结果
    private StringBuffer result = new StringBuffer();

    /**
     * 构造函数
     *
     * @param synchronous true：同步，false：异步
     */
    public ScreenCommand(boolean synchronous) {
        bSynchronous = synchronous;
    }

    /**
     * 默认构造函数，默认是同步执行
     */
    public ScreenCommand() {
        bSynchronous = true;
    }

    /**
     * 还没开始执行，和已经执行完成 这两种情况都返回false
     *
     * @return 是否正在执行
     */
    public boolean isRunning() {
        return bRunning;
    }

    /**
     * @return 返回执行结果
     */
    public String getResult() {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return new String(result);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 执行命令
     *
     * @param command eg: cat /sdcard/test.txt
     * @param maxTime 最大等待时间 (ms)
     * @return this
     */
    public ScreenCommand run(String command, final int maxTime) {
        Log.i("auto", "run command:" + command + ",maxtime:" + maxTime);
        if (command == null || command.length() == 0) {
            return this;
        }

        try {
            process = getRuntime().exec("su");
        } catch (Exception e) {
            return this;
        }
        bRunning = true;
        successResult = process.getInputStream();
        errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        os = new DataOutputStream(process.getOutputStream());

        try {
            //向sh写入要执行的命令
            os.write(command.getBytes());
            os.writeBytes("\n");
            os.flush();

            os.writeBytes("exit\n");
            os.flush();

            os.close();
            //如果等待时间设置为非正，就不开启超时关闭功能

            //开一个线程来处理input流
            final Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    String line;
                    Lock writeLock = lock.writeLock();

                    try {
                        writeLock.lock();
                        bitmap = BitmapFactory.decodeStream(successResult);
                        writeLock.unlock();
                        android.util.Log.d("www", "bitmap 22 : " + bitmap);
                    } catch (Exception e) {
                        Log.i("auto", "read InputStream exception:" + e.toString());
                    } finally {
                        try {
                            successResult.close();
                        } catch (Exception e) {
                            Log.i("auto", "close InputStream exception:" + e.toString());
                        }
                    }
                }
            });
            t1.start();

            //开一个线程来处理error流
            final Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    String line;
                    Lock writeLock = lock.writeLock();
                    try {
                        while ((line = errorResult.readLine()) != null) {
                            line += "\n";
                            writeLock.lock();
                            result.append(line);
                            writeLock.unlock();
                        }
                    } catch (Exception e) {
                        Log.i("auto", "read ErrorStream exception:" + e.toString());
                    } finally {
                        try {
                            errorResult.close();
                        } catch (Exception e) {
                            Log.i("auto", "read ErrorStream exception:" + e.toString());
                        }
                    }
                }
            });
            t2.start();

            t1.join();
            t2.join();

            try {
                int ret = process.exitValue();
            } catch (IllegalThreadStateException e) {
                Log.i("auto", "take maxTime,forced to destroy process");
                process.destroy();
            }
        } catch (Exception e) {
            Log.i("auto", "run command process exception:" + e.toString());
        }
        return this;
    }
}