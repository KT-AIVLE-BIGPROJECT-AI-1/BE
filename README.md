# CISP - AI 경찰 수사 지원 시스템

## 프로젝트 소개

CISP는 CCTV 영상, 피의자·참고인 녹취, 수사 문서 등 비정형 수사 데이터를 통합 분석하여 수사관의 판단과 문서 작성을 지원하는 AI 에이전트 서비스입니다.

AI가 진술 간 모순을 탐지하고, 영상·문서 기반 사건 정보를 구조화하며, 법리 판단 기준 평가와 수사 보고서 초안 생성을 지원합니다.

## 주요 기능

- 사건 생성, 조회, 담당자 및 상태 관리
- 수사 자료 파일 업로드 및 목록 조회
- 음성 전사와 핵심 진술 추출
- 진술 간 모순 탐지 및 상태 관리
- 영상·이미지 기반 사건 이벤트 분석
- 법리 판단 기준 조회 및 충족 여부 평가
- AI 챗봇 기반 사건 구간 질의
- 중간보고서, 수사결과보고서, 수사결정서 초안 생성
- 활동 로그 및 감사 로그 관리
- JWT 기반 인증 및 역할별 접근 제어

## 기술 스택

- Backend: Java 21, Spring Boot, Spring Security, JWT, Spring Data JPA, Spring Web MVC, Spring WebFlux
- Database: PostgreSQL
- Storage: AWS S3
- AI: GPT-4o, GPT-4o-mini, Gemini 1.5 Pro
- Embedding: text-embedding-3-small
- Deploy: Docker, AWS ECR, ECS, CodeBuild

## 프로젝트 구조

```text
src/main/java/com/aivle/big_project
├── user                # 인증, 사용자, 관리자
├── cases               # 사건 관리
├── investigationFile   # 수사 자료 파일 관리
├── analysis            # AI 분석 상태 및 결과
├── videoEvent          # 영상 이벤트
├── contradiction       # 진술 모순 탐지
├── criteria            # 법리 판단 기준 및 평가 이력
├── chat_log            # AI 챗봇 대화
├── report              # 수사 보고서 및 결정서
├── final_analysis      # 최종 통합 분석
├── audit_log           # 감사 로그 및 접근 제어
└── global              # 공통 응답, 예외, 보안
```

## 시스템 흐름

```text
Frontend
   ↓
Spring Boot Backend
   ├── PostgreSQL
   ├── AWS S3
   └── AI Server
       ├── GPT-4o / GPT-4o-mini
       │   └── 진술 분석, 모순 탐지, 법리 평가, 챗봇, 보고서 생성
       ├── Gemini 1.5 Pro
       │   └── 영상 분석, 음성 전사
       └── text-embedding-3-small
           └── 형법 RAG 임베딩
```
