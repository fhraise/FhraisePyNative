sealed class Result private constructor(internal val action: Action) {
    sealed class RegisterResult private constructor(): Result(Action.Register) {
        data object Success : RegisterResult()
    }
}
