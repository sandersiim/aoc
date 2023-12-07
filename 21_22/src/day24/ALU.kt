package day24

class ALU {
  private var memory = mutableMapOf(
    Variable.w to 0L,
    Variable.x to 0L,
    Variable.y to 0L,
    Variable.z to 0L,
  )
  private fun reset() {
    Variable.values().forEach { memory[it] = 0 }
  }

  fun printMemory() {
    println("w: ${getVar(Variable.w)}, x: ${getVar(Variable.x)}, y: ${getVar(Variable.y)}, z: ${getVar(Variable.z)}")
  }

  fun getVar(v: Variable) = memory[v]!!
  private fun setVar(v: Variable, value: Long) {
    memory[v] = value
  }

  fun runCode(commands: List<Command>, inputs: Iterator<Long>) {
    reset()
    commands.forEach {
      val arg1Value = getVar(it.arg1)
      val arg2Value = when(it.arg2) {
        is Int -> (it.arg2 as Int).toLong()
        is Variable -> getVar(it.arg2 as Variable)
        is Unit -> 0L
        else -> throw Error()
      }
      val result = when (it) {
        is AddCommand -> arg1Value + arg2Value
        is MulCommand -> arg1Value * arg2Value
        is DivCommand -> arg1Value / arg2Value
        is ModCommand -> arg1Value.mod(arg2Value)
        is EqlCommand -> if(arg1Value == arg2Value) 1L else 0L
        is InpCommand -> inputs.next()
      }
      setVar(it.arg1, result)
    }
  }
}

enum class Variable {
  w,
  x,
  y,
  z
}

sealed interface Command {
  val arg1: Variable
  val arg2: Any
}

data class InpCommand(override val arg1: Variable) : Command {
  companion object { val name get() = "inp" }
  override val arg2 = Unit
}

data class AddCommand(override val arg1: Variable, override val arg2: Any) : Command {
  companion object { val name get() = "add" }
}

data class MulCommand(override val arg1: Variable, override val arg2: Any) : Command {
  companion object { val name get() = "mul" }
}

data class DivCommand(override val arg1: Variable, override val arg2: Any) : Command {
  companion object { val name get() = "div" }
}

data class ModCommand(override val arg1: Variable, override val arg2: Any) : Command {
  companion object { val name get() = "mod" }
}

data class EqlCommand(override val arg1: Variable, override val arg2: Any) : Command {
  companion object { val name get() = "eql" }
}
