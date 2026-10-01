# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

Spring Boot 3.5 / Java 17 기반 REST API(`kr.pe.ryudaewan.brano`). MyBatis, Spring Security, Bean Validation, springdoc-openapi를 사용하며 Maven 래퍼로 빌드한다. 코드 주석, 로그 메시지, 테스트 `@DisplayName`은 한글로 작성하는 것이 관례이니 따를 것.

## 명령어

bash에서는 `./mvnw`, Windows 셸에서는 `mvnw.cmd`를 사용한다.

> 주의: 저장소에 `.mvn/wrapper/maven-wrapper.properties`가 없어서 현재 `mvnw`는 동작하지 않는다. 대신 시스템 `mvn`(`C:\Tools\maven`)을 쓰면 된다. 대상 JDK는 17이다(시스템 `JAVA_HOME` = JDK 17). JDK를 바꾸지 말고 Java 17 범위 안의 문법·API만 사용할 것.

```bash
./mvnw spring-boot:run                      # 기본 "local" 프로파일로 실행 (H2 인메모리 DB)
./mvnw test                                 # 전체 테스트
./mvnw test -Dtest=MessageControllerTest    # 테스트 클래스 하나만 실행
./mvnw test -Dtest=MessageControllerTest#findMessages_Success   # 테스트 메서드 하나만 실행
./mvnw package -Pdev                        # dev용 빌드 (H2 대신 PostgreSQL 드라이버 포함)
```

- Maven 프로파일 `local`(기본값, H2 + devtools) / `dev` / `stg` / `prd`(PostgreSQL 드라이버)는 **의존성**을 결정하고, 같은 이름의 Spring 프로파일(`application-{profile}.yaml`)은 **런타임 설정**을 결정한다. Spring 기본 프로파일은 `local`.
- `dev`/`stg`/`prd` yaml에는 현재 datasource 설정이 없고 `spring.sql.init.mode: never`이므로, datasource는 외부에서 주입해야 한다.
- Swagger UI: `/swagger-ui.html`, OpenAPI JSON: `/api-docs`. H2 콘솔은 `SecurityConfigLocal`에서 접근을 허용한다.

## 아키텍처

기능별 패키지 구조: 도메인(`user`, `message`)마다 `controller/`, `service/`, `dao/`를 둔다. 공통 기반 클래스는 `base/`, 프레임워크 설정은 `config/`에 있다.

- **VO와 예외는 service 패키지에 둔다.** `*Vo` 클래스는 `service/`에 있고 `base.service.CommonVo`(`createdAt`/`updatedAt`/`deletedAt`)를 상속한다. 도메인 예외(예: `DuplicateUserException`)도 `service/`에 둔다.
- **DAO는 MyBatis `@Mapper` 인터페이스**이며 XML 매퍼는 `src/main/resources/mapper/kr/pe/ryudaewan/brano/<도메인>/dao/<이름>Dao.xml`에 둔다(위치 패턴은 `application.yaml`에 정의, 파일명은 반드시 `Dao.xml`로 끝나야 함). resultMap은 `kr.pe.ryudaewan.brano.base.dao.CommonDao.commonResultMap`을 상속한다(`base/dao/CommonDao.xml`에 정의되어 있으며 Java `CommonDao` 인터페이스는 없음). `map-underscore-to-camel-case`가 켜져 있다.
- **모든 삭제는 논리 삭제**: 삭제 SQL은 `deleted_at = now()`로 갱신하고, 조회 SQL은 `deleted_at is null`로 거른다.
- **ID는 SQL에서 생성**: `<selectKey order="BEFORE">select coalesce(max(id), -1) + 1 ...` 방식이며 시퀀스/identity 컬럼은 쓰지 않는다.
- **H2는 PostgreSQL 호환 모드로 동작**(`MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE`)하므로 SQL은 PostgreSQL 호환으로 작성한다. 로컬 DB는 `schema.sql` / `data.sql`로 초기화된다.
- 컨트롤러는 서비스가 `null`/빈 결과를 반환하면 `ResponseEntity`로 404를 반환한다. 서비스는 잘못된 수정 요청이나 대상이 없을 때 예외 대신 `null`을 반환한다.

### 메시지 및 예외 처리 (공통 관심사)

사용자에게 보이는 모든 메시지(`data.sql`에 넣어 둔 Spring Security 기본 메시지 번들 포함)는 `.properties` 파일이 아니라 DB의 `messages` 테이블에 `(locale, message_key)` 키로 저장된다. 새 오류/검증 메시지를 추가하려면 `data.sql`에 행을 추가한다.

- `config.RDBMessageSource`가 `messageSource` 빈이다. 기동 시 전체 메시지를 메모리 캐시에 올리고, 캐시에 없으면 DB를 조회한다. 조회 키에는 `locale.getLanguage()`(예: `ko`)를 사용한다.
- `config.ValidationConfig` + `MessageSourceInterpolator`가 Bean Validation의 `{메시지.키}` 템플릿을 `RDBMessageSource`로 해석하게 한다. 인터폴레이터는 `키 + "|!^#|" + 메시지`를 반환하고, `GlobalExceptionHandler`가 이 구분자로 잘라 `MethodArgumentNotValidException`에 대해 `[{messageKey, messageContent, timestamp}]`를 응답한다.
- 비즈니스 오류: 메시지 키를 `errorCode`로 하여 `base.service.BusinessException(errorCode, args...)`의 하위 클래스를 던진다. `GlobalExceptionHandler`는 `NoSuchDataException` → 404, `DuplicateException` → 409, 그 외 `BusinessException` → 422로 매핑하고 메시지는 `RDBMessageSource`로 해석한다. 서비스는 Spring의 `DuplicateKeyException`을 도메인별 `Duplicate*Exception`으로 변환한다.

### 보안

현재 `SecurityConfigLocal`(`@Profile("local")`)만 있다. CSRF 비활성화, 정적 리소스/H2/Swagger 경로는 허용, 그 외는 인증 필요.

## 테스트 관례

- 컨트롤러 테스트: `@WebMvcTest(XController.class)` + `@Import({SecurityConfigLocal.class, TestH2Config.class})` + `@WithMockUser`. 서비스**와** `RDBMessageSource`를 `@MockitoBean`으로 모킹한다(오류 응답 본문을 검증할 때는 `messageSource.getMessage(...)`를 스텁).
- 서비스 테스트: 순수 Mockito(`@ExtendWith(MockitoExtension.class)`, DAO는 `@Mock`, 서비스는 `@InjectMocks`).

## Git 워크플로

README에 따라 GitLab Flow를 따른다. 장기 브랜치: `feature`(기본/통합), `main`, `stage`, `production`. 작업은 이슈 번호 브랜치(예: `8`)에서 하고 PR로 `feature`에 병합한다.
