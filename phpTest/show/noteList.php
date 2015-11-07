<?php
    $page = $_REQUEST['page'];
    $uid = $_REQUEST['uid'];
    $hash = $_REQUEST['hash'];
    $data = array();
    
    $pdo = null;
    try {
        $pdo = new PDO('mysql:host=localhost;dbname=tanxyzco_db;charset=utf8', 'tanxyzco_dk', 'emp631763');
        $pdo->setAttribute(PDO::ATTR_EMULATE_PREPARES, false); // 使用本地预处理（php5.3.6+默认为false）

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
        global $pdo, $data;
        $uid = $user['uid'];
        $query = $pdo->prepare("SELECT * FROM note WHERE uid=?");
        $list = array();
        if ($query->execute(array($uid))) {
            foreach ($query->fetchAll() as $row) {
                $note = array('noteId' => $row['noteId'], 'title' => $row['title'], 'content' => $row['content'], 'modifyTime' => $row['modifyTime']);
                array_push($list, $note);
            }
        }
        $data = array('list' => $list);
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
            case 100: $status = 0; $msg = '登录信息已过期'; break;
            case 101: $status = 0; $msg = '用户不存在'; break;
            default: $status = 0; $msg = '数据错误'; break;
        }
        $errmsg = array('errno' => $errno, 'msg' => $msg);
        $result = array('errmsg' => $errmsg, 'status' => $status, 'data' => $data);
        die(json_encode($result));
    }
?>