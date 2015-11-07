<?php
    $phone = $_REQUEST['phone'];
    $userName = $_REQUEST['userName'];
    $password = $_REQUEST['password'];
    $vCode = intval($_REQUEST['vCode']);
    $data = array();
    
    $pdo = null;
    try {
        $pdo = new PDO('mysql:host=localhost;dbname=tanxyzco_db;charset=utf8', 'tanxyzco_dk', 'emp631763');
        $pdo->setAttribute(PDO::ATTR_EMULATE_PREPARES, false); // 使用本地预处理（php5.3.6+默认为false）

        // 检查已注册
        $query = $pdo->prepare("SELECT phone FROM user WHERE phone=?");
        if ($query->execute(array($phone)) && $row = $query->fetch()) {
            done(201);
        }
        $query = $pdo->prepare("SELECT phone FROM user WHERE userName=?");
        if ($query->execute(array($userName)) && $row = $query->fetch()) {
            done(200);
        }

        $currentTime = time();
        $deadline = $currentTime - 600;
        // 清除过期数据
        $pdo->exec("DELETE FROM v_code WHERE createTime<=$deadline");
        // 检查验证码
        $query = $pdo->prepare("SELECT vCode FROM v_code WHERE phone=?");
        if ($query->execute(array($phone)) && $row = $query->fetch()) {
            if ($row['vCode'] != $vCode) {
                done(204);
            }
        }
        else {
            done(205);
        }

        // 检查密码
        if (strlen($password) > 100) {
            done(206);
        }
        
        // 创建用户
        $hash = generateHash();
        $query = $pdo->prepare("INSERT INTO user (userName, phone, hash, password) VALUES (?, ?, ?, ?)");
        if ($query->execute(array($userName, $phone, $hash, $password))) {
            $uid = $pdo->lastInsertId();
            $data = array('uid' => "{$uid}", 'userName' => $userName, 'phone' => $phone, 'hash' => $hash);
            done(0);
        }
        else { // 创建失败
            done(1);
        }

    } catch (PDOException $e) {
        done(1);
    }

    
    function done($errno)
    {
        global $pdo, $data;
        $pdo = null;
        $msg = '';
        $status = 1;
        switch ($errno) {
            case 0: break;
            case 200: $status = 0; $msg = '该用户名已被注册'; break;
            case 201: $status = 0; $msg = '该手机号已注册过了'; break;
            case 204: $status = 0; $msg = '验证码错误'; break;
            case 205: $status = 0; $msg = '验证码过期了'; break;
            case 206: $status = 0; $msg = '密码过长'; break;
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