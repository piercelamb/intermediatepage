package models

import org.mindrot.jbcrypt.BCrypt
import scalikejdbc._

case class Account(id: Int, email: String, password: String, name: String, role: Role)
case class newAccount(email: String, password: String, firstName: String, role: String)

object Account extends SQLSyntaxSupport[Account] {

  val a = syntax("a")

  def apply(a: SyntaxProvider[Account])(rs: WrappedResultSet): Account = apply(a.resultName)(rs)
  def apply(a: ResultName[Account])(rs: WrappedResultSet): Account = new Account(
    id         = rs.int(a.id),
    email      = rs.string(a.email),
    password   = rs.string(a.password),
    name       = rs.string(a.name),
    role       = Role.valueOf(rs.string(a.role))
  )

  private val auto = AutoSession

  def authenticate(email: String, password: String)(implicit s: DBSession = auto): Option[Account] = {
    findByEmail(email).filter { account => BCrypt.checkpw(password, account.password) }
  }

  def findByEmail(email: String)(implicit s: DBSession = auto): Option[Account] = withSQL {
    select.from(Account as a).where.eq(a.email, email)
  }.map(Account(a)).single.apply()

  def findById(id: Int)(implicit s: DBSession = auto): Option[Account] = withSQL {
    select.from(Account as a).where.eq(a.id, id)
  }.map(Account(a)).single.apply()

  def findAll()(implicit s: DBSession = auto): Seq[Account] = withSQL {
    select.from(Account as a)
  }.map(Account(a)).list.apply()

  def create(account: Account)(implicit s: DBSession = auto) {
    withSQL {
      import account._
      val pass = BCrypt.hashpw(account.password, BCrypt.gensalt())
      insert.into(Account).values(id, email, pass, name, role.toString)
    }.update.apply()
  }

  private val ac = Account.column

  def addNewAccount(email: String, password: String, firstName: String, role: Role)(implicit s: DBSession = auto) {
    val id = withSQL {
      val pass = BCrypt.hashpw(password, BCrypt.gensalt())
      QueryDSL.insert.into(Account).namedValues(
        ac.email -> email,
        ac.password -> pass,
        ac.name -> firstName,
        ac.role -> role.toString()
      )
    }.updateAndReturnGeneratedKey.apply()
  }

}
