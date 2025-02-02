package tc.oc.pgm.modules;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.logging.Logger;
import org.jdom2.Document;
import tc.oc.pgm.filters.BlockFilter;
import tc.oc.pgm.map.MapModule;
import tc.oc.pgm.map.MapModuleContext;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchModule;
import tc.oc.pgm.module.ModuleDescription;
import tc.oc.pgm.util.XMLUtils;
import tc.oc.xml.InvalidXMLException;
import tc.oc.xml.Node;

@ModuleDescription(name = "Item Destroy")
public class ItemDestroyModule extends MapModule {
  protected final Set<BlockFilter> itemsToRemove;

  public ItemDestroyModule(Set<BlockFilter> itemsToRemove) {
    this.itemsToRemove = itemsToRemove;
  }

  @Override
  public MatchModule createMatchModule(Match match) {
    return new ItemDestroyMatchModule(match, this.itemsToRemove);
  }

  // ---------------------
  // ---- XML Parsing ----
  // ---------------------

  public static ItemDestroyModule parse(MapModuleContext context, Logger logger, Document doc)
      throws InvalidXMLException {
    Set<BlockFilter> itemsToRemove = Sets.newHashSet();
    for (Node itemRemoveNode :
        Node.fromChildren(doc.getRootElement(), "item-remove", "itemremove")) {
      for (Node itemNode : Node.fromChildren(itemRemoveNode.getElement(), "item")) {
        itemsToRemove.add(new BlockFilter(XMLUtils.parseMaterialPattern(itemNode)));
      }
    }
    if (itemsToRemove.size() == 0) {
      return null;
    } else {
      return new ItemDestroyModule(itemsToRemove);
    }
  }
}
