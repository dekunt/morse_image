<?php
    $phone = $_REQUEST['phone'];
    $password = $_REQUEST['password'];
    $vCode = intval($_REQUEST['vCode']);
    $data = array();
    
    $pdo = null;
    try {
        $pdo = new PDO('mysql:host=localhost;dbname=tanxyzco_db;charset=utf8', 'tanxyzco_dk', 'emp631763');
        $pdo->setAttribute(PDO::ATTR_EMULATE_PREPARES, false); // 使用本地预处理（php5.3.6+默认为false）

        // 检查手机号
        $uid = 0;
        if (!is_numeric($phone)) {
            done(207);
        }
        $query = $pdo->prepare("SELECT uid FROM user WHERE phone=?");
        if ($query->execute(array($phone)) && $row = $query->fetch()) {
            $uid = $row['uid'];
        }
        else {
            done(208);
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
        // 修改密码
        $query = $pdo->prepare("UPDATE user SET password=? WHERE uid=?");
        if ($query->execute(array($password, $uid))) {
            done(0);
        }
        else {
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
            case 204: $status = 0; $msg = '验证码错误'; break;
            case 205: $status = 0; $msg = '验证码过期了'; break;
            case 206: $status = 0; $msg = '密码过长'; break;
            case 207: $status = 0; $msg = '手机号格式不对'; break;
            case 208: $status = 0; $msg = '手机号未注册'; break;
            default: $status = 0; $msg = '数据错误'; break;
        }
        $errmsg = array('errno' => $errno, 'msg' => $msg);
        $result = array('errmsg' => $errmsg, 'status' => $status, 'data' => $data);
        die(json_encode($result));
    }
?>