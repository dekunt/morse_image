<?php
    $nameOrPhone = $_REQUEST['nameOrPhone'];
    $password = $_REQUEST['password'];
    $data = array();
    
    $pdo = null;
    try {
        $pdo = new PDO('mysql:host=localhost;dbname=tanxyzco_db;charset=utf8', 'tanxyzco_dk', 'emp631763');
        $pdo->setAttribute(PDO::ATTR_EMULATE_PREPARES, false); // 使用本地预处理（php5.3.6+默认为false）

        // 检查用户名/手机号
        $query = null;
        if (is_numeric($nameOrPhone)) {   
            $query = $pdo->prepare("SELECT * FROM user WHERE phone=?");
        }
        else {
            $query = $pdo->prepare("SELECT * FROM user WHERE userName=?");
        }
        if ($query->execute(array($nameOrPhone)) && $row = $query->fetch()) {
            if ($row['password'] == $password) { // 检查密码
               onGotUser($row);
            }
            else {
                done(302);
            }
        }
        else {
            done(301);
        }

    } catch (PDOException $e) {
        done(1);
    }

    function onGotUser($user)
    {
        global $pdo, $data;

        // 更新hash
        $hash = generateHash();
        $uid = $user['uid'];
        $query = $pdo->exec("UPDATE user SET hash='$hash' WHERE uid=$uid");
        
        $data = array('uid' => "{$uid}", 'userName' => $user['userName'], 'phone' => $user['phone'], 'hash' => $hash);
        done(0);
    }

    
    function done($errno)
    {
        global $pdo, $data;
        $pdo = null;
        $msg = '';
        $status = 1;
        switch ($errno) {
            case 0: break;
            case 301: $status = 0; $msg = '用户不存在'; break;
            case 302: $status = 0; $msg = '密码错误'; break;
            default: $status = 0; $msg = '数据错误'; break;
        }
        $errmsg = array('errno' => $errno, 'msg' => $msg);
        $result = array('errmsg' => $errmsg, 'status' => $status, 'data' => $data);
        die(json_encode($result));
    }

    function generateHash($length = 16) {
        $chars = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
        $hash = '';
        for ($i = 0; $i < $length; $i++) {
            $hash .= $chars[mt_rand(0, strlen($chars) - 1)];
        }
        return $hash;
    }
?>