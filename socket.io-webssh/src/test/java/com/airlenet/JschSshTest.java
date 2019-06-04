package com.airlenet;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class JschSshTest {


    //    @Test
    public void connectSsh() throws JSchException {
        Session session = new JSch().getSession("0.0.0.0");
        session.setTimeout(60000);

        session.setUserInfo(new UserInfo() {
            @Override
            public String getPassphrase() {
                return null;
            }

            @Override
            public String getPassword() {
                return "123456";
            }

            @Override
            public boolean promptPassword(String s) {
                return true;
            }

            @Override
            public boolean promptPassphrase(String s) {
                return false;
            }

            @Override
            public boolean promptYesNo(String s) {

                return true;
            }

            @Override
            public void showMessage(String s) {

            }
        });
        session.connect(60000);
        session.setServerAliveInterval(60000);
        session.openChannel("shell");
    }
}
