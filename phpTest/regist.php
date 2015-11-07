<?php
    $phone = $_REQUEST['phone'];
    $userName = $_REQUEST['userName'];
    $password = $_REQUEST['password'];
    $vCode = intval($_REQUEST['vCode']);
    //$sqlPhone = mysql_escape_string($phone);
    //$sqlUserName = mysql_escape_string($userName);
    //$sqlPassword = mysql_escape_string($password);
    $sqlPhone = $phone;
    $sqlUserName = $userName;
    $sqlPassword = $password;
    $data = array();
    
    $con = mysql_connect("localhost","tanxyzco_dk","emp631763");
    if (!$con) {
        done(1);
    }
    mysql_select_db("tanxyzco_db", $con);
    // 检查已注册
    $result = mysql_query("SELECT phone FROM user WHERE phone='".$sqlPhone."'");
    while ($result && $row = mysql_fetch_array($result)){
        done(201);
    }
    $result = mysql_query("SELECT phone FROM user WHERE userName='".$sqlUserName."'");
    while ($result && $row = mysql_fetch_array($result)){
        done(200);
    }

    $currentTime = time();
    $deadline = $currentTime - 600;
    // 清除过期数据
    mysql_query("DELETE FROM v_code WHERE createTime<=$deadline");
    // 检查验证码
    $result2 = mysql_query("SELECT vCode FROM v_code WHERE phone='".$sqlPhone."'");
    $tempExist = FALSE;
    while ($result2 && $row = mysql_fetch_array($result2)){
        if ($row['vCode'] != $vCode) {
            done(204);
        }
        $tempExist = TRUE;
    }
    if (!$tempExist) {
        done(205);
    }

    // 检查密码
    if (strlen($password) > 100) {
        done(206);
    }
    
    // 创建用户
    $hash = generateHash();
    $boolResult = mysql_query("INSERT INTO user (userName, phone, hash, password) VALUES ('".$sqlUserName."', '".$sqlPhone."', '".$hash."', '".$sqlPassword."')");
    if ($boolResult) {
        $uid = mysql_insert_id();
        $data = array('uid' => "{$uid}", 'userName' => $userName, 'phone' => $phone, 'hash' => $hash);
        done(0);
    }
    else { // 创建失败
        done(1);
    }
    
    function done($errno)
    {
        global $con, $data;
        mysql_close($con);
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