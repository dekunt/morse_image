<?php
    $deleteIds = $_REQUEST['deleteIds'];
    $uid = $_REQUEST['uid'];
    $hash = $_REQUEST['hash'];
    $data = array();
    
    if ($deleteIds == null || strlen($deleteIds) == 0) {
        done(501);
    }
    $tempArray = explode(',', $deleteIds);
    $deleteArray = array();
    if ($tempArray == null || count($tempArray) == 0) {
        if (is_numeric($deleteIds)) {
            $deleteArray = array(intval($deleteIds));
        }
        else {
            done(501);
        }
    }
    else {
        foreach ($tempArray as $noteId) {
            if (is_numeric($noteId)) {
                array_push($deleteArray, intval($noteId));
            }
            else {
                done(501);
            }
        }
    }

    $pdo = null;
    try {
        $pdo = new PDO('mysql:host=localhost;dbname=tanxyzco_db;charset=utf8mb4', 'tanxyzco_dk', 'emp631763');
        $pdo->setAttribute(PDO::ATTR_EMULATE_PREPARES, false); // 使用本地预处理（php5.3.6+默认为false）
        $pdo->exec("SET character_set_client = utf8mb4");
        $pdo->exec("SET character_set_connection = utf8mb4");
        $pdo->exec("SET character_set_database = utf8mb4");
        $pdo->exec("SET character_set_results = utf8mb4");
        $pdo->exec("SET character_set_server = utf8mb4");
        $pdo->exec("SET character_set_system = utf8mb4");
        $pdo->exec("SET collation_connection = utf8mb4_unicode_ci");
        $pdo->exec("SET collation_database = utf8mb4_unicode_ci");
        $pdo->exec("SET collation_server = utf8mb4_unicode_ci");

        // 检查用户
        $query = $pdo->prepare("SELECT * FROM user WHERE uid=?");
        if ($query->execute(array($uid)) && $row = $query->fetch()) {
            if ($row['hash'] == $hash) {
               onGotUser($row);
            }
            else {
                done(100);
            }
        }
        else {
            done(101);
        }

    } catch (PDOException $e) {
        done(1);
    }

    function onGotUser($user)
    {
        global $pdo, $data, $deleteArray;
        $uid = $user['uid'];

        foreach ($deleteArray as $noteId) {
            $query = $pdo->prepare("DELETE FROM note WHERE noteId=? AND uid=?");
            if (!$query->execute(array($noteId, $uid))) {
                done(1);
            }
        }
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
            case 501: $status = 0; $msg = '参数错误'; break;
            case 100: $status = 0; $msg = '登录信息已过期'; break;
            case 101: $status = 0; $msg = '用户不存在'; break;
            default: $status = 0; $msg = '数据错误'; break;
        }
        $errmsg = array('errno' => $errno, 'msg' => $msg);
        $result = array('errmsg' => $errmsg, 'status' => $status, 'data' => $data);
        die(json_encode($result));
    }
?>