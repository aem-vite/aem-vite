package dev.aemvite.aem.context.mocks;

import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import org.apache.sling.api.SlingHttpServletRequest;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockHtmlLibraryManager implements HtmlLibraryManager {
  private final ClientLibrary mockClientLibrary;

  public MockHtmlLibraryManager() {
    mockClientLibrary = mock(ClientLibrary.class);
  }

  public static final String LIB_PATH = "/apps/aem-vite/clientlibs/test";

  @Override
  public void writeJsInclude(SlingHttpServletRequest slingHttpServletRequest, Writer writer, String... strings) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeJsInclude(SlingHttpServletRequest slingHttpServletRequest, Writer writer, boolean b, String... strings) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeCssInclude(SlingHttpServletRequest slingHttpServletRequest, Writer writer, String... strings) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeCssInclude(SlingHttpServletRequest slingHttpServletRequest, Writer writer, boolean b, String... strings) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeThemeInclude(SlingHttpServletRequest slingHttpServletRequest, Writer writer, String... strings) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeIncludes(SlingHttpServletRequest slingHttpServletRequest, Writer writer, String... strings) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public HtmlLibrary getLibrary(LibraryType libraryType, String s) {
    return null;
  }

  @Override
  public HtmlLibrary getLibrary(SlingHttpServletRequest slingHttpServletRequest) {
    return null;
  }

  @Override
  public boolean isMinifyEnabled() {
    return false;
  }

  @Override
  public boolean isDebugEnabled() {
    return false;
  }

  @Override
  public boolean isGzipEnabled() {
    return false;
  }

  @Override
  public Map<String, ClientLibrary> getLibraries() {
    return null;
  }

  @Override
  public Collection<ClientLibrary> getLibraries(String[] strings, LibraryType libraryType, boolean b, boolean b1) {
    when(mockClientLibrary.allowProxy()).thenReturn(true);
    when(mockClientLibrary.getCategories()).thenReturn(new String[]{"test.clientlib"});
    when(mockClientLibrary.getIncludePath(LibraryType.JS, false)).thenReturn(LIB_PATH + LibraryType.JS.extension);
    when(mockClientLibrary.getPath()).thenReturn(LIB_PATH);

    return Collections.singletonList(mockClientLibrary);
  }

  @Override
  public Collection<ClientLibrary> getThemeLibraries(String[] strings, LibraryType libraryType, String s, boolean b) {
    return null;
  }

  @Override
  public void invalidateOutputCache() throws RepositoryException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void ensureCached() throws IOException, RepositoryException {
    throw new UnsupportedOperationException();
  }
}
