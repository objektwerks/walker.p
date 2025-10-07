package walker.pane

import scalafx.Includes.*
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, SelectionMode, Tab, TabPane, TableColumn, TableView}
import scalafx.scene.layout.{HBox, Priority, VBox}

import walker.{Context, Model, Walker}
import walker.dialog.WalkerDialog

final class WalkersPane(context: Context, model: Model) extends VBox:
  spacing = 6
  padding = Insets(6)

  val tableView = new TableView[Walker]():
    columns ++= List(
      new TableColumn[Walker, String]:
        prefWidth = 320
        text = context.headerName
        cellValueFactory = _.value.nameProperty
    )
    items = model.observableWalkers

  val addButton = new Button:
    graphic = context.addImage
    text = context.buttonAdd
    disable = false
    onAction = { _ => add() }

  val editButton = new Button:
    graphic = context.editImage
    text = context.buttonEdit
    disable = true
    onAction = { _ => update() }

  val buttonBar = new HBox:
    spacing = 6
    children = List(addButton, editButton)
  
  val tab = new Tab:
  	text = context.tabWalkers
  	closable = false
  	content = new VBox {
      spacing = 6
      padding = Insets(6)
      children = List(tableView, buttonBar)
    }

  val tabPane = new TabPane:
    tabs = List(tab)

  children = List(tabPane)
  VBox.setVgrow(tableView, Priority.Always)
  VBox.setVgrow(tabPane, Priority.Always)

  tableView.onMouseClicked = { event =>
    if (event.getClickCount == 2 && tableView.selectionModel().getSelectedItem != null) update()
  }

  tableView.selectionModel().selectionModeProperty.value = SelectionMode.Single
  
  tableView.selectionModel().selectedItemProperty().addListener { (_, _, selectedItem) =>
    // model.update executes a remove and add on items. the remove passes a null selectedItem!
    if selectedItem != null then
      model.selectedWalkerId.value = selectedItem.id
      editButton.disable = false
    else editButton.disable = true
  }

  def add(): Unit =
    WalkerDialog(context, Walker(name = "")).showAndWait() match
      case Some(walker: Walker) =>
        model.add(walker)
        tableView.selectionModel().select(0)
      case _ =>

  def update(): Unit =
    if tableView.selectionModel().getSelectedItem != null then
      val selectedIndex = tableView.selectionModel().getSelectedIndex
      val walker = tableView.selectionModel().getSelectedItem.walker
      WalkerDialog(context, walker).showAndWait() match
        case Some(updatedWalker: Walker) =>
          model.update(walker, updatedWalker)
          tableView.selectionModel().select(selectedIndex)
        case _ =>