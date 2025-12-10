insert into users (uid, name, email, created_at, updated_at, deleted_at)
values (0, '관리자', 'admin@brano.com', now(), null, null);
insert into users (uid, name, email, created_at, updated_at, deleted_at)
values (1, '장원영', 'think@brano.com', now(), null, null);
insert into users (uid, name, email, created_at, updated_at, deleted_at)
values (2, '하지원', 'hahaha@brano.com', now(), null, null);
insert into users (uid, name, email, created_at, updated_at, deleted_at)
values ( 3, '오인혜', 'inhye@brano.com'
       , to_timestamp('2019-04-23 13:34:41', 'YYYY-MM-DD HH24:MI:SS')
       , to_timestamp('2019-05-21 18:12:13', 'YYYY-MM-DD HH24:MI:SS')
       , to_timestamp('2020-09-14 22:59:59', 'YYYY-MM-DD HH24:MI:SS'));

-- 메시지 샘플 데이터
INSERT INTO messages (message_id, locale, message_key, message_content, created_at)
VALUES (0, 'ko', 'validation.name.required', '이름은 필수입니다', now())
     , (1, 'ko', 'validation.email.invalid', '올바른 이메일을 입력하세요', now())
     , (2, 'ko', 'validation.email.required', '이메일은 필수입니다', now())
     , (3, 'ko', 'user.dup.email', '이미 쓰는 이메일로 새 사용자 등록 시도', now())
     , (4, 'ko', 'message.not.exist', '없는 메시지 코드입니다', now())
;