<?php
    $noteId = array_key_exists('noteId', $_REQUEST) ? $_REQUEST['noteId'] : null;
    $title = $_REQUEST['title'];
    $content = $_REQUEST['content'];
    $uid = $_REQUEST['uid'];
    $hash = $_REQUEST['hash'];
    $data = array();
    
    $pdo = null;
    if (strlen($title) == 0 && strlen($content) == 0) {
        done(401);
    }
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
        global $pdo, $data, $noteId, $title, $content;
        $uid = $user['uid'];

        // 检查noteId
        if ($noteId) {
            $query = $pdo->prepare("SELECT noteId, createTime FROM note WHERE noteId=?");
            if ($query->execute(array($noteId)) && $row = $query->fetch()) {
                onGotNote($row);
            }
        }

        $currentTime = time();
        $query = $pdo->prepare("INSERT INTO note (uid, title, content, createTime) VALUES (?, ?, ?, ?)");
        if ($query->execute(array($uid, $title, $content, $currentTime))) {
            $noteId = $pdo->lastInsertId();
            $data = array('noteId' => "{$noteId}", 'uid' => "{$uid}", 'title' => $title, 'content' => $content, 'createTime' => $currentTime);
            done(0);
        }
        done(1);
    }

    function onGotNote($note)
    {
        global $pdo, $data, $title, $content, $uid;
        $noteId = $note['noteId'];
        $query = $pdo->prepare("UPDATE note SET title=?, content=? WHERE noteId=?");
        if ($query->execute(array($title, $content, $noteId))) {
            $data = array('noteId' => "{$noteId}", 'uid' => $uid, 'title' => $title, 'content' => $content, 'createTime' => $note['createTime']);
            done(0);
        }
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
            case 401: $status = 0; $msg = '内容不能为空'; break;
            case 100: $status = 0; $msg = '登录信息已过期'; break;
            case 101: $status = 0; $msg = '用户不存在'; break;
            default: $status = 0; $msg = '数据错误'; break;
        }
        $errmsg = array('errno' => $errno, 'msg' => $msg);
        $result = array('errmsg' => $errmsg, 'status' => $status, 'data' => $data);
        die(json_encode($result));
    }
?>