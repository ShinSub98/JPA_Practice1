### 의존성 옵션
- runtimeOnly / implementation
  - **runtimeOnly**
    - 범위: 런타임
    - 목적: 애플리케이션 실행 시에만 필요한 의존성. 컴파일 시에는 불필요. (DB 등)
  - **implementation**
    - 범위: 컴파일 및 런타임
    - 목적: 컴파일 및 애플리케이션 실행 모두에 필요한 의존성.